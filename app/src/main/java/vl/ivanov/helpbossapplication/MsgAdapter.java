package vl.ivanov.helpbossapplication;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.MessageViewHolder> {
    ArrayList<Messages> msg;

    public MsgAdapter(ArrayList<Messages> msg) {
        this.msg = msg;
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView textMsg;
        LinearLayout line1_1;

        public MessageViewHolder(View view) {
            super(view);
            textMsg = (TextView) view.findViewById(R.id.messageChat);
            line1_1 = (LinearLayout) view.findViewById(R.id.line1_1);
        }

    }


    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int ViewType) {
        View view = null;

        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.out_message_container, viewGroup, false);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        int id_sender = msg.get(position).getSender();

        if (id_sender == 1) {
            holder.line1_1.setGravity(Gravity.LEFT);
        } else {
            holder.line1_1.setGravity(Gravity.RIGHT);
        }

        holder.textMsg.setText(msg.get(position).getMsg());
    }

    @Override
    public int getItemCount() {
        return msg.size();
    }

}
