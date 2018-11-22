package automaticcallrecorder.phonerecorder.barcodeexample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import automaticcallrecorder.phonerecorder.barcodeexample.Models.Product;

public class Recycleadapter extends RecyclerView.Adapter<Recycleadapter.viewholder> {

    Context con;
    productsdatabase database;
    List<Product> mylist;
    public Recycleadapter(Context context)
    {
        this.con=context;
        database=new productsdatabase(context);
        mylist=database.getdataforproduct();
        notifyDataSetChanged();

    }


    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(con.getApplicationContext()).inflate(R.layout.rowrecycle,parent,false);
        viewholder vholder=new viewholder(view);
        return vholder;
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {

        holder.namee.setText(mylist.get(position).getProductName());
        holder.numberr.setText(mylist.get(position).getBarcodeNumber());

        Glide.with(con)
                .load(mylist.get(position).getImages().get(0))
                .into(holder.productimage);

    }

    @Override
    public int getItemCount() {
        return mylist.size();
    }

    class viewholder extends RecyclerView.ViewHolder{

        TextView namee , numberr , pricee ;
        ImageView productimage,removeimg;

        public viewholder(View itemView) {
            super(itemView);

            namee=itemView.findViewById(R.id.nameproduct);
            numberr=itemView.findViewById(R.id.numberproduct);
            pricee=itemView.findViewById(R.id.productprice);
            productimage=itemView.findViewById(R.id.productimg);
            removeimg=itemView.findViewById(R.id.remove);

        }
    }
}
