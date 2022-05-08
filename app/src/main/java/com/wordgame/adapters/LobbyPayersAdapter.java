package com.wordgame.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wordgame.R;
import com.wordgame.models.Player;

import java.util.List;

/**
 * Adapter for players in lobby.
 *
 * @author Arunova Anastasia
 * @version 1.0
 * @since 1.0
 */
public class LobbyPayersAdapter extends BaseAdapter {

    // List of players.
    private List<Player> players;

    private LayoutInflater layoutInflater;

    /**
     * Constructor.
     *
     * @param context context
     * @param players players list
     */
    public LobbyPayersAdapter(Context context, List<Player> players) {
        this.players = players;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Set players.
     *
     * @param players players
     */
    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    @Override
    public int getCount() {
        return players.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_players_lobby, parent, false);
        }

        TextView nickname = view.findViewById(R.id.list_view_players_lobby_tv);
        if (players.get(position) != null) {
            nickname.setText(players.get(position).getNickname());
        }

        return view;
    }
}
