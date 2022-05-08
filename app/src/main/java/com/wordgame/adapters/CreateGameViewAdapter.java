package com.wordgame.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wordgame.R;

import java.util.ArrayList;


/**
 * Adapter for game creation screen.
 *
 * @author Arunova Margarita
 * @version 1.0
 * @since 1.0
 */
public class CreateGameViewAdapter extends RecyclerView.Adapter<CreateGameViewAdapter.CreateGameViewHolder> {

    private final ArrayList<Integer> layouts;
    private final ArrayList<Integer> parameters;

    public CreateGameViewAdapter() {
        layouts = new ArrayList<>(4);
        layouts.add(R.layout.round_number_layout);
        layouts.add(R.layout.seconds_number_layout);
        layouts.add(R.layout.players_number_layout);
        layouts.add(R.layout.game_language_layout);

        parameters = new ArrayList<>(4);
        parameters.add(1);
        parameters.add(30);
        parameters.add(5);
        parameters.add(0);
    }

    @NonNull
    @Override
    public CreateGameViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(viewType, viewGroup, false);
        return new CreateGameViewHolder(view, parameters);
    }

    @Override
    public void onBindViewHolder(@NonNull CreateGameViewHolder viewHolder, int position) {
        viewHolder.onGameCreatePageChange(position);
    }

    @Override
    public int getItemViewType(int position) {
        return layouts.get(position);
    }

    @Override
    public int getItemCount() {
        return layouts.size();
    }

    public ArrayList<Integer> getParameters() {
        return parameters;
    }

    private interface GameCreatePageChangeListener {
        void onGameCreatePageChange(int position);
    }

    public static class CreateGameViewHolder extends RecyclerView.ViewHolder implements GameCreatePageChangeListener {
        private final View itemView;
        private final ArrayList<Integer> parameters;

        public CreateGameViewHolder(@NonNull final View itemView, ArrayList<Integer> parameters) {
            super(itemView);
            this.itemView = itemView;
            this.parameters = parameters;
        }

        @Override
        public void onGameCreatePageChange(int position) {
            switch (position) {
                case 0: {
                    processRoundAmountPage();
                    break;
                }
                case 1: {
                    processSecondsAmountPage();
                    break;
                }
                case 2: {
                    processPlayersAmountPage();
                    break;
                }
                case 3: {
                    processGameLanguagePage();
                    break;
                }
            }
        }

        private void processRoundAmountPage() {
            final TextView roundsAmount = itemView.findViewById(R.id.rounds_number);

            itemView.findViewById(R.id.round_minus).setOnClickListener(view -> {
                int currentAmount = Integer.parseInt(String.valueOf(roundsAmount.getText()));
                if (currentAmount > 1) {
                    roundsAmount.setText(String.valueOf(currentAmount - 1));
                    parameters.set(0, currentAmount - 1);
                }
            });
            itemView.findViewById(R.id.round_plus).setOnClickListener(view -> {
                int currentAmount = Integer.parseInt(String.valueOf(roundsAmount.getText()));
                if (currentAmount < 5) {
                    roundsAmount.setText(String.valueOf(currentAmount + 1));
                    parameters.set(0, currentAmount + 1);
                }
            });
        }

        private void processSecondsAmountPage() {
            SeekBar seekBarSeconds = itemView.findViewById(R.id.seconds_seek_bar);
            final TextView secondsAmount = itemView.findViewById(R.id.seconds_number);

            seekBarSeconds.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    int currentAmount = Integer.parseInt(String.valueOf(seekBar.getProgress()));
                    secondsAmount.setText(String.valueOf(currentAmount + 10));
                    parameters.set(1, currentAmount + 10);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    int currentAmount = Integer.parseInt(String.valueOf(seekBar.getProgress()));
                    secondsAmount.setText(String.valueOf(currentAmount + 10));
                    parameters.set(1, currentAmount + 10);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int currentAmount = Integer.parseInt(String.valueOf(seekBar.getProgress()));
                    secondsAmount.setText(String.valueOf(currentAmount + 10));
                    parameters.set(1, currentAmount + 10);
                }
            });
        }

        private void processPlayersAmountPage() {
            SeekBar seekBarPlayers = itemView.findViewById(R.id.players_seek_bar);
            final TextView playersAmount = itemView.findViewById(R.id.players_number);

            seekBarPlayers.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    int currentAmount = Integer.parseInt(String.valueOf(seekBar.getProgress()));
                    playersAmount.setText(String.valueOf(currentAmount + 1));
                    parameters.set(2, currentAmount + 1);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    int currentAmount = Integer.parseInt(String.valueOf(seekBar.getProgress()));
                    playersAmount.setText(String.valueOf(currentAmount + 1));
                    parameters.set(2, currentAmount + 1);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int currentAmount = Integer.parseInt(String.valueOf(seekBar.getProgress()));
                    playersAmount.setText(String.valueOf(currentAmount + 1));
                    parameters.set(2, currentAmount + 1);
                }
            });
        }

        private void processGameLanguagePage() {
            RadioGroup languageRadioGroup = itemView.findViewById(R.id.language_radio_group);
            RadioButton radioButtonEng = itemView.findViewById(R.id.eng_radiobutton);
            RadioButton radioButtonRu = itemView.findViewById(R.id.ru_radiobutton);

            languageRadioGroup.check(R.id.eng_radiobutton);

            radioButtonEng.setOnClickListener(view -> parameters.set(3, 0));
            radioButtonRu.setOnClickListener(view -> parameters.set(3, 1));
        }
    }
}


