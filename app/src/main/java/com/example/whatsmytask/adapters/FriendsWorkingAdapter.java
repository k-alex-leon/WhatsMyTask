package com.example.whatsmytask.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsmytask.R;
import com.example.whatsmytask.models.TaskU;
import com.example.whatsmytask.models.User;
import com.example.whatsmytask.providers.AuthProvider;
import com.example.whatsmytask.providers.TaskProvider;
import com.example.whatsmytask.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsWorkingAdapter extends RecyclerView.Adapter<FriendsWorkingAdapter.ViewHolder> {

    Context context;
    UsersProvider mUserProvider;
    AuthProvider mAuthProvider;
    ArrayList<String> friendsList;

    public FriendsWorkingAdapter(ArrayList<String> friendsList, Context context) {
        this.context = context;
        this.friendsList = friendsList;
        mAuthProvider = new AuthProvider();
        mUserProvider = new UsersProvider();

    }

    @NonNull
    @Override
    public FriendsWorkingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_friends_working,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsWorkingAdapter.ViewHolder holder, int position) {

        mUserProvider.getUser(friendsList.get(position)).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() && !documentSnapshot.getString("id").equals(mAuthProvider.getUid())){
                    if (documentSnapshot.contains("userName")){
                        holder.mTxtVFriendWorkingName.setText(documentSnapshot.getString("userName"));
                    }
                    if (documentSnapshot.contains("imageProfile")){
                        Picasso.with(context).load(documentSnapshot.getString("imageProfile")).into(holder.mCImgVFriendWorking);
                    }

                    holder.mCImgVDeleteFriendWorking.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO terminar metodos para eliminar contacto de la tarea
                            showConfirmRemoveFriend();
                        }
                    });
                }else{
                    holder.mCardVFriendWorking.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showConfirmRemoveFriend() {

    }


    @Override
    public int getItemCount() {
        for (int i = 0; i < friendsList.size(); i++) {
            if (mAuthProvider.getUid().equals(friendsList.get(i))){
                friendsList.remove(i);
            }
        }
        return friendsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView mCardVFriendWorking;
        TextView mTxtVFriendWorkingName;
        CircleImageView mCImgVFriendWorking, mCImgVDeleteFriendWorking;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mCardVFriendWorking = itemView.findViewById(R.id.cardVFriendWorking);
            mTxtVFriendWorkingName = itemView.findViewById(R.id.txtVFriendWorkingName);
            mCImgVFriendWorking = itemView.findViewById(R.id.cImgVFriendWorking);
            mCImgVDeleteFriendWorking = itemView.findViewById(R.id.cImgVDeleteFriendWorking);
        }
    }
}
