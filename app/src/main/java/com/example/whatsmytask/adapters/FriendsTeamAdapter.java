package com.example.whatsmytask.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsmytask.R;
import com.example.whatsmytask.models.User;
import com.example.whatsmytask.providers.AuthProvider;
import com.example.whatsmytask.providers.UsersProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsTeamAdapter extends RecyclerView.Adapter<FriendsTeamAdapter.ViewHolder> {

    Context context;
    UsersProvider mUserProvider;
    AuthProvider mAuthProvider;
    ArrayList<String> friendsList;
    String mIdTask;

    public FriendsTeamAdapter(ArrayList<String> friendsList, Context context, String idTask){
        this.context = context;
        this.friendsList = friendsList;
        mAuthProvider = new AuthProvider();
        mUserProvider = new UsersProvider();
        mIdTask = idTask;
    }

    @NonNull
    @Override
    public FriendsTeamAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_friends_team,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsTeamAdapter.ViewHolder holder, int position) {
        mUserProvider.getUser(friendsList.get(position)).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    User user = documentSnapshot.toObject(User.class);
                    holder.txtVFriendNameTeamAdapter.setText(user.getUserName());
                    if (documentSnapshot.contains("imageProfile")){
                        Picasso.with(context).load(user.getImageProfile()).into(holder.cImgVFriendTeamAdapter);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView cImgVFriendTeamAdapter;
        TextView txtVFriendNameTeamAdapter;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cImgVFriendTeamAdapter = itemView.findViewById(R.id.cImgVFriendTeamAdapter);
            txtVFriendNameTeamAdapter = itemView.findViewById(R.id.txtVFriendNameTeamAdapter);
        }
    }
}
