package com.example.kindnestapp2;

import android.content.Intent;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class DonationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_CATEGORY = 0;
    private static final int TYPE_SUBCATEGORY = 1;
    private final List<Object> displayList;
    private final String ngoName;
    private final DonationActivity activity;

    public static class SubCategoryItem {
        public final String name;
        public SubCategoryItem(String name) { this.name = name; }
    }

    public DonationAdapter(DonationActivity activity, List<Object> displayList, String ngoName) {
        this.activity = activity;
        this.displayList = displayList;
        this.ngoName = ngoName;
    }

    @Override
    public int getItemViewType(int position) {
        if (displayList.get(position) instanceof String) return TYPE_CATEGORY;
        else return TYPE_SUBCATEGORY;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_CATEGORY)
            return new CategoryViewHolder(inflater.inflate(R.layout.item_donation_category, parent, false));
        else
            return new SubCategoryViewHolder(inflater.inflate(R.layout.item_donation_subcategory, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_CATEGORY)
            ((CategoryViewHolder) holder).bind((String) displayList.get(position));
        else
            ((SubCategoryViewHolder) holder).bind((SubCategoryItem) displayList.get(position));
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    // --- CATEGORY ICONS ---
    private int getIconForCategory(String categoryName) {
        switch (categoryName.toLowerCase()) {
            case "health_and_medical": return R.drawable.healthicon;
            case "education": return R.drawable.educationicon;
            case "poverty_alleviation_and_livelihood":
            case "human_rights_and_social_justice": return R.drawable.humanrightsicon;
            case "food_and_nutrition": return R.drawable.nutrition;
            case "shelter_and_disaster_relief": return R.drawable.sheltericon;
            case "women_empowerment": return R.drawable.womenicon;
            case "environment_and_nature": return R.drawable.natureicon;
            case "childrens_welfare": return R.drawable.childrenicon;
            default: return R.drawable.defaulticon;
        }
    }

    // --- SUBCATEGORY ICONS ---
    private int getIconForSubcategory(String subCategoryName) {
        switch (subCategoryName.toLowerCase()) {
            // Education & Youth
            case "sponsor_a_childs_education_annual":
            case "primary_schooling_for_a_child":
            case "school_for_underprivileged_children": return R.drawable.icon_school;
            case "digital_school_program":
            case "digital_learning_centers":
            case "digital_literacy_class": return R.drawable.icon_digital_literacy;
            case "school_supplies_and_uniforms": return R.drawable.icon_school_supplies;
            case "library_and_book_donation": return R.drawable.icon_books;
            case "adolescent_development_program":
            case "youth_development_programs": return R.drawable.icon_youth_development;
            case "support_for_underprivileged_students": return R.drawable.icon_student_support;
            case "free_academic_coaching": return R.drawable.icon_coaching;
            case "sponsor_a_teacher": return R.drawable.icon_teacher;
            case "non_formal_education": return R.drawable.icon_literacy;
            case "extracurricular_activities_fund": return R.drawable.icon_activities;

            // Food & Nutrition
            case "one_taka_meal_program":
            case "daily_meals_for_the_underprivileged": return R.drawable.icon_food_meal;
            case "emergency_food_packages":
            case "emergency_food_aid": return R.drawable.icon_food_package;
            case "food_for_orphanages": return R.drawable.icon_food_orphanage;
            case "mid_day_meals_program": return R.drawable.icon_lunch;
            case "qurbani_meat_distribution": return R.drawable.icon_qurbani;
            case "iftar_and_sehri_during_ramadan": return R.drawable.icon_ramadan;
            case "nutritional_support":
            case "nutritional_support_and_meals": return R.drawable.icon_nutrition;

            // Health & Medical
            case "maternal_and_child_health_program": return R.drawable.icon_mother_child;
            case "healthcare_for_street_children":
            case "medical_camps_in_disaster_areas": return R.drawable.icon_medical_kit;
            case "community_health_worker_training": return R.drawable.icon_health_team;
            case "tuberculosis_and_malaria_control": return R.drawable.icon_disease_control;
            case "eye_care_and_catact_surgery":
            case "free_eye_camps": return R.drawable.icon_eye;
            case "public_health_awareness_campaigns": return R.drawable.icon_awareness;
            case "sanitation_and_hygiene_kits_wash":
            case "health_and_hygiene_for_women":
            case "hygiene_and_sanitation_kits": return R.drawable.icon_hygien;
            case "clean_drinking_water_projects": return R.drawable.icon_water_drop;

            // Livelihood & Poverty
            case "microfinance_and_small_business_loans": return R.drawable.icon_microfinance;
            case "agricultural_training_for_farmers": return R.drawable.icon_agriculture;
            case "skills_development_for_youth": return R.drawable.icon_skills;
            case "ultra_poor_graduation_program": return R.drawable.icon_graduation;

            // Shelter & Relief
            case "build_a_home_for_a_homeless_family": return R.drawable.icon_home_build;
            case "winter_blanket_drive":
            case "winter_clothing_and_blanket_drive": return R.drawable.icon_winter_blanket;
            case "flood_and_cyclone_relief": return R.drawable.icon_flood_relief;
            case "emergency_shelter_tents": return R.drawable.icon_emergency_tent;
            case "sanitation_facilities_for_the_displaced": return R.drawable.icon_sanitation;

            // Women & Human Rights
            case "vocational_skills_training_tailoring": return R.drawable.icon_sewing;
            case "support_for_female_entrepreneurs": return R.drawable.icon_business_woman;
            case "awareness_against_domestic_violence": return R.drawable.icon_stop_violence;
            case "free_legal_services_for_the_poor": return R.drawable.icon_legal_aid;
            case "rights_advocacy_for_minorities": return R.drawable.icon_advocacy;
            case "support_for_old_age_homes": return R.drawable.icon_elderly_care;

            // Environment
            case "tree_plantation_drives": return R.drawable.icon_tree_plant;
            case "river_and_beach_cleanup_projects": return R.drawable.icon_cleanup;
            case "wildlife_conservation_awareness": return R.drawable.icon_wildlife;
            case "waste_management_programs": return R.drawable.icon_waste_management;

            // Children
            case "drop_in_centers_for_street_children": return R.drawable.icon_dropin_center;
            case "rehabilitation_and_counseling": return R.drawable.icon_counseling;
            case "protection_from_child_labor": return R.drawable.icon_child_protection;
            case "reunite_child_with_family": return R.drawable.icon_family_reunion;

            default: return R.drawable.defaulticon;
        }
    }

    // --- VIEWHOLDERS ---
    class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryIcon;
        TextView categoryTitle;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryIcon = itemView.findViewById(R.id.category_icon);
            categoryTitle = itemView.findViewById(R.id.category_title);
        }

        void bind(String titleWithUnderscores) {
            String cleanTitle = titleWithUnderscores.replace('_', ' ');
            categoryTitle.setText(cleanTitle);
            categoryIcon.setImageResource(getIconForCategory(titleWithUnderscores));
        }
    }

    class SubCategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView subCategoryIcon;
        TextView subCategoryName;
        MaterialButton donateItemButton;

        SubCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            subCategoryIcon = itemView.findViewById(R.id.subcategory_icon);
            subCategoryName = itemView.findViewById(R.id.subcategory_name);
            donateItemButton = itemView.findViewById(R.id.donate_item_button);
        }

        void bind(SubCategoryItem item) {
            subCategoryIcon.setImageResource(getIconForSubcategory(item.name));
            String cleanSubCategoryName = item.name.replace('_', ' ');
            subCategoryName.setText(cleanSubCategoryName);
            donateItemButton.setOnClickListener(v -> showAmountDialog(item));
        }

        private void showAmountDialog(SubCategoryItem item) {
            String cleanDialogTitle = item.name.replace('_', ' ');
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Donate for: " + cleanDialogTitle);
            builder.setMessage("Enter the amount (BDT) you wish to donate:");
            final EditText input = new EditText(activity);
            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            builder.setView(input);

            builder.setPositiveButton("Proceed to Pay", (dialog, which) -> {
                String amountStr = input.getText().toString().trim();
                if (amountStr.isEmpty()) {
                    Toast.makeText(activity, "Please enter an amount", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    double amount = Double.parseDouble(amountStr);
                    // Launch DonationFormActivity with data
                    Intent intent = new Intent(activity, DonationFormActivity.class);
                    intent.putExtra("NGO_NAME", ngoName);
                    intent.putExtra("SUBCATEGORY", cleanDialogTitle);
                    intent.putExtra("AMOUNT", amount);
                    activity.startActivity(intent);
                } catch (NumberFormatException e) {
                    Toast.makeText(activity, "Invalid amount", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        }
    }
}
