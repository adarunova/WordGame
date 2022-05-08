package com.wordgame.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wordgame.R;
import com.wordgame.models.Player;

import java.util.List;

/**
 * Adapter for results list.
 *
 * @author Arunova Margarita
 * @version 1.0
 * @since 1.0
 */
public class ResultsListAdapter extends ArrayAdapter<Pair<Player, Integer>> {
    private final Context context;
    private final int resource;

    public ResultsListAdapter(@NonNull Context context, int resource, @NonNull List<Pair<Player, Integer>> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if ((view = convertView) == null) {
            view = LayoutInflater.from(context).inflate(resource, null);
        }

        final Pair<Player, Integer> player = getItem(position);
        final TextView playerName = view.findViewById(R.id.results_name);
        final TextView playerScore = view.findViewById(R.id.results_score);
        final TextView playerPlace = view.findViewById(R.id.results_place);

        playerName.setText(player.first.getNickname());
        playerScore.setText("" + player.first.getPoints());
        playerPlace.setText("" + player.second);

        return view;
    }
}
