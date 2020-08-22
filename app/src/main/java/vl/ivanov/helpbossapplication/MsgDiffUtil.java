package vl.ivanov.helpbossapplication;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class MsgDiffUtil extends DiffUtil.Callback {
    private final List<Messages> oldList;
    private final List<Messages> newList;

    public MsgDiffUtil(List<Messages> oldList, List<Messages> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Messages oldProduct = oldList.get(oldItemPosition);
        Messages newProduct = newList.get(newItemPosition);
        return oldProduct.getId() == newProduct.getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Messages oldProduct = oldList.get(oldItemPosition);
        Messages newProduct = newList.get(newItemPosition);
        return oldProduct.getMsg().equals(newProduct.getMsg());
    }
}
