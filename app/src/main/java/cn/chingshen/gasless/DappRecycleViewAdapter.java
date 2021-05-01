package cn.chingshen.gasless;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.List;

import cn.chingshen.gasless.databinding.ItemDappBinding;
import cn.chingshen.gasless.domain.vos.Dapp;
import cn.chingshen.gasless.domain.vos.EthPrice;
import cn.chingshen.gasless.domain.vos.GasNow;

public class DappRecycleViewAdapter extends RecyclerView.Adapter<DappRecycleViewAdapter.ViewHolder> {

    private Context context;
    private List<Dapp> dapps;
    MainActivityViewModel viewModel;

    public DappRecycleViewAdapter(AppCompatActivity context, List<Dapp> dapps) {
        this.context = context;
        this.dapps = dapps;

        viewModel = new ViewModelProvider(context).get(MainActivityViewModel.class);

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
        Dapp dapp = dapps.get(position);
        double price = 0.0;
        GasNow gasNow = viewModel.getGasNow().getValue();
        EthPrice ethPrice = viewModel.getEthPrice().getValue();
        String currency = viewModel.getCurrency().getValue();
        if (dapp != null && gasNow != null && ethPrice != null) {
            String format = "0.00";
            price = dapp.getTx()[0].getGas() * gasNow.getFast() / 10e17;
            if (currency.equals("ETH")) {
                format = "0.0000";
            } else {
                price = price * (double) ethPrice.getUsd();
            }
            DecimalFormat df = new DecimalFormat(format);
            String priceStr = df.format(price);
            dapp.setPriceStr(priceStr);
            holder.binding.setDapp(dapp);
            Glide.with(context).load(dapp.getIcon()).into(holder.binding.logo);
        }
    }

    @Override
    public long getItemId(int position) {
        return dapps.get(position).getTitle().hashCode();
    }

    @Override
    public int getItemCount() {
        return dapps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemDappBinding binding;

        public ViewHolder(@NonNull ItemDappBinding dataBinding) {
            super(dataBinding.getRoot());
            this.binding = dataBinding;
        }
    }
}
