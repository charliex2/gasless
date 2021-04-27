package cn.chingshen.gasless;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import cn.chingshen.gasless.databinding.ItemDappBinding;
import cn.chingshen.gasless.domain.vos.Dapp;

public class DappRecycleViewAdapter extends RecyclerView.Adapter<DappRecycleViewAdapter.ViewHolder> {

    private Context context;
    private Dapp[] dapps;

    public DappRecycleViewAdapter(Context context, Dapp[] dapps) {
        this.context = context;
        this.dapps = dapps;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemDappBinding dataBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_dapp, parent, false);

        return new ViewHolder(dataBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Dapp dapp = dapps[position];
        holder.binding.name.setText(dapp.getTitle());
    }

    @Override
    public int getItemCount() {
        return dapps.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemDappBinding binding;

        public ViewHolder(@NonNull ItemDappBinding dataBinding) {
            super(dataBinding.getRoot());
            this.binding = dataBinding;
        }
    }
}
