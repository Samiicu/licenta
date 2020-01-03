package com.example.samuel.pentrufacultate.models;

import androidx.annotation.Nullable;

public class StringHelper {
    public static final String USER_UID_EXTRA = "userUid";
    public static final int POSTION_SHOPPING_LIST_SETTINGS_GROUP = 4;
    public static final int POSTION_SUBMENU_ITEM_SEND_SHOPPING_LIST = 1;
    public static final int REQUEST_CODE_QR_READER = 7;
    public static final int RESULT_QR_READER_SUCCESS = 1;
    public static final int RESULT_QR_READER_FAILED = 0;
    public static final String RESULT_QR_READER = "result_qr_reader";
    public static final String TAG_DISPLAY_ONE_RECIPE_FRAGMENT = "display_one_recipe_fragment";

    public static String getTag(Class parentClass, @Nullable Class childClass) {
        if (childClass == null) {
            return "TAG_SINGLE_" + parentClass.getSimpleName();
        } else {
            return "TAG_" + parentClass.getSimpleName() + "_" + childClass.getSimpleName();
        }
    }

    public static String prepareShoppingListForSms(String recipeTitle,ShoppingList shoppingList) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Lista de cumparaturi pentru "+recipeTitle+":\n\n");
        for (ShoppingItem item : shoppingList.getShoppingItems()) {
            if (item.isChecked()) {
                stringBuilder.append(item.getName())
                        .append("  ")
                        .append(item.getQuantity())
                        .append(" ")
                        .append(item.getMeasure())
                        .append("\n");
            }
        }
        return stringBuilder.toString();
    }

}
