package com.example.whatsmytask.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsmytask.R;
import com.example.whatsmytask.models.User;
import com.example.whatsmytask.providers.AuthProvider;
import com.example.whatsmytask.providers.FriendsProvider;
import com.example.whatsmytask.providers.UsersProvider;
import com.example.whatsmytask.utils.ItemChecked;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class TaskFriendAdapter extends FirestoreRecyclerAdapter<User, TaskFriendAdapter.ViewHolder>{

    Context context;
    UsersProvider mUserProvider;
    FriendsProvider mFriendProvider;
    AuthProvider mAuthProvider;

    ItemChecked itemChecked;
    ArrayList checkedFriends = new ArrayList();



    public TaskFriendAdapter(FirestoreRecyclerOptions<User> options, Context context, ItemChecked itemChecked){
        super(options);
        this.context = context;
        this.itemChecked = itemChecked;
        mUserProvider = new UsersProvider();
    }




    @Override
    protected void onBindViewHolder(@NotNull TaskFriendAdapter.ViewHolder holder, int position,@NotNull User user) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String friendId = document.getId();
        getFriendInfo(friendId, holder);

    }


    private void getFriendInfo(String friendId, TaskFriendAdapter.ViewHolder holder) {
        mUserProvider.getUser(friendId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if (documentSnapshot.contains("userName")){
                        String userName = documentSnapshot.getString("userName");
                        holder.textViewNameFriend.setText(userName.toUpperCase());
                    }
                    if (documentSnapshot.contains("imageProfile")){
                        String imageProfile = documentSnapshot.getString("imageProfile");
                        if (imageProfile != null){
                            if (!imageProfile.isEmpty()){
                                Picasso.with(context).load(imageProfile).into(holder.mImageProfileViewFriend);
                            }
                        }
                    }
                }
                holder.mCheckboxFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(holder.mCheckboxFriend.isChecked()){
                            checkedFriends.add(friendId);
                        }else if(!holder.mCheckboxFriend.isChecked()){
                            checkedFriends.remove(friendId);
                        }
                        Toast.makeText(context, checkedFriends.toString(), Toast.LENGTH_SHORT).show();
                        itemChecked.itemSelected(checkedFriends);

                    }
                });


            }
        });
    }



    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_friend_task,parent, false);
        return new ViewHolder(view);
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView textViewNameFriend;
        ImageView mImageProfileViewFriend;
        CheckBox mCheckboxFriend;

        public ViewHolder(View view){
            super(view);
            textViewNameFriend = view.findViewById(R.id.textViewUserFriend);
            mImageProfileViewFriend = view.findViewById(R.id.imageUserFriendProfile);
            mCheckboxFriend = view.findViewById(R.id.checkboxFriend);

            mFriendProvider = new FriendsProvider();
            mAuthProvider = new AuthProvider();

        }
    }
}
