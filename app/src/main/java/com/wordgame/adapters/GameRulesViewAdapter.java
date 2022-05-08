package com.wordgame.adapters;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wordgame.R;

import java.util.ArrayList;

/**
 * Adapter for game creation screen.
 *
 * @author Arunova Margarita
 * @version 1.0
 * @since 1.0
 */
public class GameRulesViewAdapter extends RecyclerView.Adapter<GameRulesViewAdapter.GameRulesViewHolder> {
    private final ArrayList<Integer> layouts;

    public GameRulesViewAdapter() {
        layouts = new ArrayList<>(4);
        layouts.add(R.layout.game_rules_first_page);
        layouts.add(R.layout.game_rules_second_page);
        layouts.add(R.layout.game_rules_third_page);
        layouts.add(R.layout.game_rules_fourth_page);
    }

    @NonNull
    @Override
    public GameRulesViewAdapter.GameRulesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(viewType, viewGroup, false);
        return new GameRulesViewAdapter.GameRulesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameRulesViewAdapter.GameRulesViewHolder viewHolder, int position) {
    }

    @Override
    public int getItemViewType(int position) {
        return layouts.get(position);
    }

    @Override
    public int getItemCount() {
        return layouts.size();
    }

    public static class GameRulesViewHolder extends RecyclerView.ViewHolder {
        public GameRulesViewHolder(@NonNull final View itemView) {
            super(itemView);
        }
    }
}
