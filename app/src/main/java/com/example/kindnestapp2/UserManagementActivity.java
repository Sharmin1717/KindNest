package com.example.kindnestapp2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> userList;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        recyclerView = findViewById(R.id.usersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        adapter = new UserAdapter(userList);
        recyclerView.setAdapter(adapter);

        loadUsers();
    }

    private void loadUsers() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        user.setUid(snapshot.getKey());
                        userList.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserManagementActivity.this, "Failed to load users: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

        private List<User> users;

        UserAdapter(List<User> users) {
            this.users = users;
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
            return new UserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            User user = users.get(position);
            holder.nameTextView.setText(user.getName());
            holder.emailTextView.setText(user.getEmail());

            // Set approval status
            boolean isApproved = user.isApproved();
            holder.statusTextView.setText(isApproved ? "Approved" : "Pending");

            holder.approveButton.setText(isApproved ? "Remove Approval" : "Approve");

            holder.approveButton.setOnClickListener(v -> {

                boolean newStatus = !isApproved;
                usersRef.child(user.getUid()).child("approved").setValue(newStatus)
                        .addOnSuccessListener(aVoid -> {
                            String message = newStatus ? "User approved successfully" : "User approval removed";
                            Toast.makeText(UserManagementActivity.this, message, Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(UserManagementActivity.this, "Failed to update user status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            });

            holder.deleteButton.setOnClickListener(v -> {
                usersRef.child(user.getUid()).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(UserManagementActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(UserManagementActivity.this, "Failed to delete user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            });
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        class UserViewHolder extends RecyclerView.ViewHolder {
            TextView nameTextView, emailTextView, statusTextView;
            Button approveButton, deleteButton;

            UserViewHolder(@NonNull View itemView) {
                super(itemView);
                nameTextView = itemView.findViewById(R.id.userNameTextView);
                emailTextView = itemView.findViewById(R.id.userEmailTextView);
                statusTextView = itemView.findViewById(R.id.userStatusTextView);
                approveButton = itemView.findViewById(R.id.approveUserButton);
                deleteButton = itemView.findViewById(R.id.deleteUserButton);
            }
        }
    }
}