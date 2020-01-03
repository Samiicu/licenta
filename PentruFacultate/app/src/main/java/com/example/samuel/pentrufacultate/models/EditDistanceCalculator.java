package com.example.samuel.pentrufacultate.models;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class EditDistanceCalculator {
    private static final String TAG = "clasificatorTAG";


    public EditDistanceCalculator() {
    }


    private int min3(int a, int b, int c) {
        if (a <= b && a <= c) return a;
        if (b <= a && b <= c) return b;
        return c;
    }

    public int editDistance(String first, String second) {
        int n = first.length();
        int m = second.length();
        int dp[][] = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= m; j++) {

                if (i == 0)
                    dp[i][j] = j;
                else if (j == 0)
                    dp[i][j] = i;

                else if (first.charAt(i - 1) == second.charAt(j - 1))
                    dp[i][j] = dp[i - 1][j - 1];
                else
                    dp[i][j] = 1 + min3(dp[i][j - 1], dp[i - 1][j - 1], dp[i - 1][j]);

            }
        }

        return dp[n][m];
    }

    public String getMostProbableAction(String actionFromVoiceCommand) {

        String givenAction = actionFromVoiceCommand.toLowerCase();

        String[] startProcedure = {"start", "can you start procedure"};
        String[] nextStep = {"next", "next step", "forward", "next step please"};
        String[] backStep = {"back", "back step", "backward", "previous step"};
        String[] repeatStep = {"repeat", "repeat step", "repeat the current step please", "repeat please"};
        String[] restartProcedure = {"restart procedure"};
        String[] gotToStep = {"go to step", "go at step"};

        HashMap<String, String[]> actionGroups = new HashMap<>();
        actionGroups.put("startProcedure", startProcedure);
        actionGroups.put("nextStep", nextStep);
        actionGroups.put("backStep", backStep);
        actionGroups.put("repeatStep", repeatStep);
        actionGroups.put("restartProcedure", restartProcedure);
        actionGroups.put("goToStep", gotToStep);

        String bestAction = null;
        int minEditDistance = -1;
        //Verify all Action that App know how to response
        for (Map.Entry<String, String[]> actionGroup : actionGroups.entrySet()) {
            // one action type
            String[] sameActions = actionGroup.getValue();

            //verify all action for the same type
            for (String action : sameActions) {

                int edAction2Input = editDistance(givenAction, action);

                int minLen = min(givenAction.length(), action.length());

                if ((edAction2Input < minEditDistance || minEditDistance == -1)
                        && edAction2Input <= minLen / 2) {

                    minEditDistance = edAction2Input;
                    bestAction = actionGroup.getKey();
                }
            }

        }
        return bestAction;

    }

    private int min(int a, int b) {
        return b < a ? b : a;
    }

    public String prepareComandForED(String command) {
        return command.replaceAll("[^A-Za-z0-9]+", "").toLowerCase();
    }

}
