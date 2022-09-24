package com.example.whatsmytask.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsmytask.R;
import com.example.whatsmytask.models.User;
import com.example.whatsmytask.providers.AuthProvider;
import com.example.whatsmytask.providers.TaskProvider;
import com.example.whatsmytask.providers.UsersProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsWorkingAdapter extends RecyclerView.Adapter<FriendsWorkingAdapter.ViewHolder> {

    Context context;
    UsersProvider mUserProvider;
    AuthProvider mAuthProvider;
    TaskProvider mTaskProvider;
    ArrayList<String> friendsList;
    String mIdTask;

    public FriendsWorkingAdapter(ArrayList<String> friendsList, Context context, String idTask) {
        this.context = context;
        this.friendsList = friendsList;
        mAuthProvider = new AuthProvider();
        mUserProvider = new UsersProvider();
        mTaskProvider = new TaskProvider();
        mIdTask = idTask;
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
                User user = documentSnapshot.toObject(User.class);
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
                            showConfirmRemoveFriend(user);
                        }
                    });
                }
            }
        });
    }

    private void showConfirmRemoveFriend(User user) {
        friendsList.add(mAuthProvider.getUid());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.friend_dialog, null);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        // quitamos el background del dialog para pasarle el custom_border
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        ImageView imgVCloseRemove;
        CircleImageView cImgVFriend;
        TextView txtVFriendName , txtVTitleRemoveFriendDialog;
        Button btnCancelRemove, btnRemove;

        imgVCloseRemove = view.findViewById(R.id.imgVCloseFriendDialog);
        cImgVFriend = view.findViewById(R.id.cImgVFriendDialog);
        txtVFriendName = view.findViewById(R.id.txtVNameFriendDialog);
        txtVTitleRemoveFriendDialog = view.findViewById(R.id.txtVTitleFriendDialog);
        btnCancelRemove = view.findViewById(R.id.btnCancelFriendDialog);
        btnRemove = view.findViewById(R.id.btnAcceptFriendDialog);

        txtVTitleRemoveFriendDialog.setText("REMOVE FROM TASK");
        Picasso.with(context).load(user.getImageProfile()).into(cImgVFriend);
        txtVFriendName.setText(user.getUserName());

        imgVCloseRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendsList.remove(mAuthProvider.getUid());
                dialog.dismiss();
            }
        });

        btnCancelRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendsList.remove(mAuthProvider.getUid());
                dialog.dismiss();
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                removeFriend(user.getId());
            }
        });

    }

    // quitando user del array y actualizar el array de la bd
    private void removeFriend(String idFriend) {

        for (int i = 0; i < friendsList.size(); i++) {
            if (idFriend.equals(friendsList.get(i))){
                friendsList.remove(i);
            }
        }

        mTaskProvider.updateFriendsTask(friendsList, mIdTask).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(context, "Friend deleted", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
