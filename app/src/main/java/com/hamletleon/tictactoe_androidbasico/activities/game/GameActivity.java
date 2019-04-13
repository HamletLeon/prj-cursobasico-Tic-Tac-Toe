package com.hamletleon.tictactoe_androidbasico.activities.game;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

import com.hamletleon.tictactoe_androidbasico.R;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    private ArrayList<ArrayList<Pair<Integer, Pair<Boolean, String>>>> matrixIds = new ArrayList<ArrayList<Pair<Integer, Pair<Boolean, String>>>>(){
        {
            add(new ArrayList<Pair<Integer, Pair<Boolean, String>>>(){
                {
                    add(new Pair<>(R.id.item00, new Pair<Boolean, String>(false, "")));
                    add(new Pair<>(R.id.item01, new Pair<Boolean, String>(false, "")));
                    add(new Pair<>(R.id.item02, new Pair<Boolean, String>(false, "")));
                }
            });
            add(new ArrayList<Pair<Integer, Pair<Boolean, String>>>(){
                {
                    add(new Pair<>(R.id.item10, new Pair<Boolean, String>(false, "")));
                    add(new Pair<>(R.id.item11, new Pair<Boolean, String>(false, "")));
                    add(new Pair<>(R.id.item12, new Pair<Boolean, String>(false, "")));
                }
            });
            add(new ArrayList<Pair<Integer, Pair<Boolean, String>>>(){
                {
                    add(new Pair<>(R.id.item20, new Pair<Boolean, String>(false, "")));
                    add(new Pair<>(R.id.item21, new Pair<Boolean, String>(false, "")));
                    add(new Pair<>(R.id.item22, new Pair<Boolean, String>(false, "")));
                }
            });
        }
    };

    private boolean turnO = true;
    private String playerWon = null;
    private AppCompatTextView whoWonAdvisor;

    private View playerOneTurn;
    private AppCompatTextView playerOneName;
    private View playerTwoTurn;
    private AppCompatTextView playerTwoName;

    private Button playAgain;

    private int notTurnColor;
    private int turnColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        whoWonAdvisor = findViewById(R.id.whoWon);

        playerOneTurn = findViewById(R.id.playerOneTurn);
        playerOneName = findViewById(R.id.playerOneName);

        playerTwoTurn = findViewById(R.id.playerTwoTurn);
        playerTwoName = findViewById(R.id.playerTwoName);

        playAgain = findViewById(R.id.playAgain);

        notTurnColor = getResources().getColor(android.R.color.holo_red_light);
        turnColor = getResources().getColor(android.R.color.holo_green_light);

        // Configurations
        playerOneName.setText(getString(R.string.player, 1));
        playerTwoName.setText(getString(R.string.player, 2));
        for(ArrayList<Pair<Integer, Pair<Boolean, String>>> itemIds : matrixIds){
            for (Pair<Integer, Pair<Boolean, String>> pair : itemIds)
            {
                AppCompatImageView item = findViewById(pair.first);
                item.setOnClickListener(this);
            }
        }
    }
    @Override
    public void onClick(View v) {
        AppCompatImageView item = (AppCompatImageView) v;
        boolean isOccupied = item.getDrawable() != null;
        String[] tag = ((String) item.getTag()).split(",");
        if (tag.length > 0 && !isOccupied && playerWon == null){
            int rowPos = Integer.parseInt(tag[0]);
            int colPos = Integer.parseInt(tag[1]);
            ArrayList<Pair<Integer, Pair<Boolean, String>>> row = matrixIds.get(rowPos);
            Pair<Integer, Pair<Boolean, String>> col = row.get(colPos);

            String turn = turnO ? "o" : "x";
            row.set(colPos, new Pair<>(col.first, new Pair<Boolean, String>(true, turn)));

            item.setImageResource(turnO ? R.drawable.o_icon : R.drawable.x_icon);
            item.setVisibility(View.VISIBLE);

            turnO = !turnO; // Show a message, reset count and check combinations if times is enough
            String whoWon = hasWonAnyPlayer();
            if (whoWon != null && !whoWon.isEmpty() && whoWon != "full"){
                playerWon = whoWon;
                String whoWonText = getString(R.string.whoWon, whoWon == "o" ? 1 : 2);
                whoWonAdvisor.setText(whoWonText);
                whoWonAdvisor.setVisibility(View.VISIBLE);
                playAgain.setVisibility(View.VISIBLE);
                playAgain.setOnClickListener(resetListener);
            } else if (whoWon == "full"){
                playAgain.setVisibility(View.VISIBLE);
                playAgain.setOnClickListener(resetListener);
            } else {
                playerOneTurn.setBackgroundColor(turnO ? turnColor : notTurnColor);
                playerTwoTurn.setBackgroundColor(turnO ? notTurnColor : turnColor);
            }
        }
    }

    private View.OnClickListener resetListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            resetAll();
        }
    };

    private String hasWonAnyPlayer(){
        ArrayList<Pair<Integer, Integer>> playerOneVerticalPattern = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> playerOneHorizontalPattern = new ArrayList<>();
        Pair<Integer, Integer> playerOneDiagonalPattern = null;

        ArrayList<Pair<Integer, Integer>> playerTwoVerticalPattern = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> playerTwoHorizontalPattern = new ArrayList<>();
        Pair<Integer, Integer> playerTwoDiagonalPattern = null;

        int count = 0;
        for(int i = 0; i < matrixIds.size(); i++){
            ArrayList<Pair<Integer, Pair<Boolean, String>>> row = matrixIds.get(i);
            for(int a = 0; a < row.size(); a++){
                Pair<Integer, Pair<Boolean, String>> col = row.get(a);
                Pair<Boolean, String> colValue = col.second;
                if (colValue.first && !colValue.second.isEmpty()){
                    Pair<Integer, Integer> pair = new Pair(i+1, a+1);
                    if (colValue.second == "o"){
                        Pair<Boolean, Pair<Integer, Integer>> won = didPlayerWon(pair, playerOneVerticalPattern, playerOneHorizontalPattern, playerOneDiagonalPattern);
                        playerOneDiagonalPattern = won.second;
                        if (won.first) return "o";
                    }
                    else{
                        Pair<Boolean, Pair<Integer, Integer>> won = didPlayerWon(pair, playerTwoVerticalPattern, playerTwoHorizontalPattern, playerTwoDiagonalPattern);
                        playerTwoDiagonalPattern = won.second;
                        if (won.first) return "x";
                    }
                    count++;
                }
            }
        }
        if(count == 9) return "full";
        return null;
    }

    private Pair<Boolean, Pair<Integer, Integer>> didPlayerWon(Pair<Integer, Integer> selected, ArrayList<Pair<Integer, Integer>> verticalPattern,
                                 ArrayList<Pair<Integer, Integer>> horizontalPattern, Pair<Integer, Integer> diagonalPattern){
        addOrSetLinearPattern(verticalPattern, selected.first, selected.second);
        addOrSetLinearPattern(horizontalPattern, selected.second, selected.first);
        Pair<Integer, Integer> diagonalPat = addOrSetDiagonalPattern(diagonalPattern, selected.first, selected.second);

        boolean userWon = false;
        for (Pair<Integer, Integer> sum : verticalPattern){ userWon = sum.second == 6; }
        for (Pair<Integer, Integer> sum : horizontalPattern){ if (!userWon) userWon = sum.second == 6; }
        if (!userWon) userWon = diagonalPat != null ? ((diagonalPat.first > 6 && diagonalPat.second > 6 && diagonalPat.first != diagonalPat.second
                && (diagonalPat.first -2 != diagonalPat.second))
                || diagonalPat.first == 6 && diagonalPat.second == 6) : false;
        return new Pair(userWon, diagonalPat);
    }

    private void addOrSetLinearPattern(ArrayList<Pair<Integer, Integer>> linearPattern, Integer first, Integer second)
    {
        Integer set = -1;
        Pair<Integer, Integer> pairToAdd = new Pair<Integer, Integer>(first, second);
        for(int i = 0; i < linearPattern.size(); i++){
            Pair<Integer, Integer> pair = linearPattern.get(i);
            if (pair.first == first)
            {
                pairToAdd = new Pair<Integer, Integer>(pair.first, pair.second + second);
                set = i;
            }
        }
        if (set != -1) linearPattern.set(set, pairToAdd);
        else linearPattern.add(pairToAdd);
    }

    // Combinations ^Equals && R1C3 R2C2 R3C1
    private Pair<Integer, Integer> addOrSetDiagonalPattern(Pair<Integer, Integer> diagonalPattern, Integer first, Integer second){
//        if (first == 0 && second == 2) diagonalPattern.add(true);
//        else if (first == 2 && second == 0) diagonalPattern.add(true);
//        else if (first == 1 && second == 1) diagonalPattern.add(true);

        Integer diagFirst = diagonalPattern != null ? diagonalPattern.first : 0;
        Integer diagSecond = diagonalPattern != null ? diagonalPattern.second : 0;
        return new Pair<Integer, Integer>(first + diagFirst, second + diagSecond);
    }

    private void resetAll(){
        recreate();
    }
}
