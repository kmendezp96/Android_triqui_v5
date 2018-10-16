package com.example.pc.android_triqui_v3;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private TicTacToeGame mGame;
    private boolean mhumanFirst;
    private int mAndroidWins;
    private int mHumanWins;
    private TextView mNumberHuman;
    private TextView mNumberAndroid;
    private boolean mGameOver;
    private TextView mInfoTextView;
    //static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;
    private BoardView mBoardView;
    private MediaPlayer mHumanMediaPlayer;
    private MediaPlayer mComputereMediaPlayer;
    private SharedPreferences mPrefs;
    private boolean mSoundOn;


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharArray("board", mGame.getBoardState());
        outState.putBoolean("mGameOver", mGameOver);
        outState.putInt("mHumanWins", Integer.valueOf(mHumanWins));
        outState.putInt("mComputerWins", Integer.valueOf(mAndroidWins));
        outState.putCharSequence("info", mInfoTextView.getText());
    }

    @Override
    protected void onResume(){
        super.onResume();
        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.humansound);
        mComputereMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.androidsound);
    }

    @Override
    protected void onPause(){
        super.onPause();

        mHumanMediaPlayer.release();
        mComputereMediaPlayer.release();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Save the current scores
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("mHumanWins", mHumanWins);
        ed.putInt("mComputerWins", mAndroidWins);
        ed.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Restore the scores from the persistent preference data source
        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        mNumberHuman = (TextView) findViewById(R.id.player_points);
        mNumberAndroid = (TextView) findViewById(R.id.android_points);
        mInfoTextView = (TextView) findViewById(R.id.information);
        mGame = new TicTacToeGame();
        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
        mHumanWins = mPrefs.getInt("mHumanWins", 0);
        mAndroidWins = mPrefs.getInt("mComputerWins", 0);
        mhumanFirst = false;

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSoundOn = mPrefs.getBoolean("sound", true);
        String difficultyLevel = mPrefs.getString("difficulty_level",
        getResources().getString(R.string.difficulty_harder));
        if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
        else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
        else
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);

        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        mBoardView.setOnTouchListener(mTouchListener);
        startNewGame();
        displayScores();

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mGame.setBoardState(savedInstanceState.getCharArray("board"));
        mGameOver = savedInstanceState.getBoolean("mGameOver");
        mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
        mHumanWins = savedInstanceState.getInt("mHumanWins");
        mAndroidWins = savedInstanceState.getInt("mComputerWins");
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.new_game:
                startNewGame();
                return true;
            /*case R.id.ai_difficulty:
                showDialog(DIALOG_DIFFICULTY_ID);
                return true;*/
            case R.id.settings:
                startActivityForResult(new Intent(this, Settings.class), 0);
                return true;
            case R.id.quit:
                showDialog(DIALOG_QUIT_ID);
                return true;
            case R.id.resetScore:
                this.mAndroidWins = 0;
                this.mHumanWins = 0;
                displayScores();
                SharedPreferences.Editor ed = mPrefs.edit();
                ed.putInt("mHumanWins", mHumanWins);
                ed.putInt("mComputerWins", mAndroidWins);
                ed.commit();
        }
        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id){
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch(id){

            /*case DIALOG_DIFFICULTY_ID:
                builder.setTitle(R.string.difficulty_choose);

                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert),
                };

                int selected =0;
                TicTacToeGame.DifficultyLevel dif;
                dif = mGame.getDifficultyLevel();

                if(dif == TicTacToeGame.DifficultyLevel.Easy){
                    selected = 0;
                } else if ( dif == TicTacToeGame.DifficultyLevel.Harder){
                    selected = 1;
                } else if ( dif == TicTacToeGame.DifficultyLevel.Expert){
                    selected = 2;
                }

                builder.setSingleChoiceItems(levels, selected, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        dialog.dismiss();

                        if(item == 0){
                            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
                        } else if ( item == 1){
                            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
                        } else if ( item == 2){
                            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
                        }

                        Toast.makeText(getApplicationContext(), levels[item],
                                Toast.LENGTH_SHORT).show();

                    }
                });
                dialog = builder.create();
                break;*/
            case DIALOG_QUIT_ID:

                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                MainActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();
                break;
        }
        return dialog;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_CANCELED) {
            // Apply potentially new settings
            mSoundOn = mPrefs.getBoolean("sound", true);
            String difficultyLevel = mPrefs.getString(
                    "difficulty_level",
            getResources().getString(R.string.difficulty_harder));
            if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
            else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
            else
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
            }
        }


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;

            if(!mGameOver && setMove(TicTacToeGame.HUMAN_PLAYER, pos)) {
                int winner = mGame.checkForWinner();
                if(winner==0){
                    mInfoTextView.setText(R.string.turn_computer);
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    winner = mGame.checkForWinner();
                }

                if(winner==0){
                    mInfoTextView.setText(R.string.turn_human);
                } else if ( winner == 1){
                    mInfoTextView.setText(R.string.result_tie);
                    mGameOver=true;
                } else if (winner == 2){
                   //mInfoTextView.setText(R.string.result_human_wins);
                    mHumanWins++;
                    mNumberHuman.setText("Human: " + mHumanWins);
                    String defaultMessage = getResources().getString(R.string.result_human_wins);
                    mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
                    mGameOver=true;
                } else {
                    mInfoTextView.setText(R.string.result_computer_wins);
                    mAndroidWins++;
                    mNumberAndroid.setText("Android: " + mAndroidWins);
                    mGameOver=true;
                }            }

            return false;
        }
    };

    private void startNewGame(){
        mGame.clearBoard();
        mBoardView.invalidate();

        mhumanFirst=!mhumanFirst;
        mGameOver=false;

        //human goes first
        if(mhumanFirst){
            mInfoTextView.setText(R.string.first_human);
        } else {
            mInfoTextView.setText(R.string.first_android);
            int move = mGame.getComputerMove();
            setMove(TicTacToeGame.COMPUTER_PLAYER,move);
            mInfoTextView.setText(R.string.turn_human);
        }
    }
    private boolean setMove(char player,int location){
        if(mGame.setMove(player, location)){
            if(player == TicTacToeGame.HUMAN_PLAYER && mSoundOn)
                mHumanMediaPlayer.start();
            else if(player == TicTacToeGame.COMPUTER_PLAYER  && mSoundOn)
                mComputereMediaPlayer.start();
            mBoardView.invalidate();
            return true;
        }
        return false;
    }

    private void displayScores() {
        mNumberHuman.setText("Human: " + mHumanWins);
        mNumberAndroid.setText("Android: " + mAndroidWins);
    }

}