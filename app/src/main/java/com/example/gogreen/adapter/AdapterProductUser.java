package com.example.gogreen.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogreen.FilterProductUser;
import com.example.gogreen.R;
import com.example.gogreen.activities.ShopDetailsActivity;
import com.example.gogreen.models.ModelProduct;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

//import p32929.androideasysql_library.Column;
//import p32929.androideasysql_library.EasyDB;

public class AdapterProductUser extends RecyclerView.Adapter<com.example.gogreen.adapter.AdapterProductUser.HolderProductUser> implements Filterable {

    public ArrayList<ModelProduct> productList,filterList;
    private Context context;
    private FilterProductUser filter;

    public AdapterProductUser(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList = productList;
    }

    @NonNull
    @Override
    public HolderProductUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_user,parent,false);
        return new HolderProductUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductUser holder, int position) {
        //get data
        final ModelProduct modelProduct = productList.get(position);
        String discountAvailable = modelProduct.getDiscountAvailable();
        String discountNote = modelProduct.getDiscountNote();
        String discountPrice = modelProduct.getDiscountPrice();
        String productCategory = modelProduct.getProductCategory();
        String originalPrice = modelProduct.getOriginalPrice();
        String productDescription = modelProduct.getProductDescription();
        String productTitle = modelProduct.getProductTitle();
        String productQuantity = modelProduct.getProductQuantity();
        String productId = modelProduct.getProductId();
        String timestamp = modelProduct.getTimestamp();
        String uid = modelProduct.getUid();
        String productIcon = modelProduct.getProductIcon();

        //set data
        holder.titleTv.setText(productTitle);
        holder.discountedNoteTv.setText(discountNote);
        holder.originalPriceTv.setText("₹"+originalPrice);
        holder.discountedPriceTv.setText("₹"+discountPrice);
        holder.descriptionTv.setText(productDescription);

        if(discountAvailable.equals("true")){
            //product is on discount
            holder.discountedPriceTv.setVisibility(View.VISIBLE);
            holder.discountedNoteTv.setVisibility(View.VISIBLE);
            holder.originalPriceTv.setPaintFlags(holder.originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); //add strike through on original price
        }
        else{
            //Product is not on discount
            holder.discountedPriceTv.setVisibility(View.GONE);
            holder.discountedNoteTv.setVisibility(View.GONE);
            holder.originalPriceTv.setPaintFlags(0);
        }

        try{
            Picasso.get().load(productIcon).placeholder(R.drawable.cartfigma).into(holder.productIconIv);
        }
        catch (Exception e){
            holder.productIconIv.setImageResource(R.drawable.cartfigma);
        }

        holder.addToCartTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add product to cart
                showQuantityDialog(modelProduct);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show product details
            }
        });
    }

    private double cost = 0, finalCost = 0;
    private  int quantity = 0;
    private void showQuantityDialog(ModelProduct modelProduct) {
        //inflate layout for dialog
        View view = LayoutInflater.from(context).inflate(R.layout.dailog_quantity,null);
        //init layout views
        ImageView productIv = view.findViewById(R.id.productIv);
        final TextView titleTv = view.findViewById(R.id.titleTv);
        TextView descriptionTv = view.findViewById(R.id.descriptionTv);
        TextView discountedNoteTv = view.findViewById(R.id.discountedNoteTv);
        final TextView originalPriceTv = view.findViewById(R.id.originalPriceTv);
        TextView priceDiscountedTv = view.findViewById(R.id.priceDiscountedTv);
        final TextView finalPriceTv = view.findViewById(R.id.finalPriceTv);
        final TextView quantityTv = view.findViewById(R.id.quantityTv);
        ImageButton decrementBtn = view.findViewById(R.id.decrementBtn);
        ImageButton incrementBtn = view.findViewById(R.id.incrementBtn);
        Button continueBtn = view.findViewById(R.id.continueBtn);

        //get data from model
        final String productId = modelProduct.getProductId();
        String title = modelProduct.getProductTitle();
        String productQuantity = modelProduct.getProductQuantity();
        String description = modelProduct.getProductDescription();
        String discountNote = modelProduct.getDiscountNote();
        String image = modelProduct.getProductIcon();

        final String price;
        if(modelProduct.getDiscountAvailable().equals("true")){
            //product have discount
            price = modelProduct.getDiscountPrice();
            discountedNoteTv.setVisibility(View.VISIBLE);
            originalPriceTv.setPaintFlags(originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); //add strike through on original price
        }
        else{
            //product don't have discount
            discountedNoteTv.setVisibility(View.GONE);
            priceDiscountedTv.setVisibility(View.GONE);
            price = modelProduct.getOriginalPrice();
        }

        cost = Double.parseDouble(price.replaceAll("₹",""));
        finalCost = Double.parseDouble(price.replaceAll("₹",""));
        quantity = 1;

        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        //set data
        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_cart_gray).into(productIv);
        }
        catch (Exception e){
            productIv.setImageResource(R.drawable.ic_cart_gray);
        }
        titleTv.setText(""+title);
        descriptionTv.setText(""+description);
        discountedNoteTv.setText(""+discountNote);
        quantityTv.setText(""+quantity);
        originalPriceTv.setText("₹"+modelProduct.getOriginalPrice());
        priceDiscountedTv.setText("₹"+modelProduct.getDiscountPrice());
        finalPriceTv.setText(""+finalCost);

        final AlertDialog dialog = builder.create();
        dialog.show();

        //increment quantity of the product
        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalCost = finalCost + cost;
                quantity++;

                finalPriceTv.setText(""+finalCost);
                quantityTv.setText(""+quantity);
            }
        });

        //decrement quantity of the product, only if quantity > 1
        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity>1){
                    finalCost = finalCost - cost;
                    quantity --;

                    finalPriceTv.setText(""+finalCost);
                    quantityTv.setText(""+quantity);
                }
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleTv.getText().toString().trim();
                String priceEach = price;
                String totalPrice = finalPriceTv.getText().toString().trim().replace("₹","");
                String quantity = quantityTv.getText().toString().trim();

                //Toast.makeText(context, ""+title, Toast.LENGTH_SHORT).show();

                //add to db(SQLite)
                addToCart(productId, title, priceEach, totalPrice, quantity);

                dialog.dismiss();
            }
        });
    }

    private int itemId = 1;
    private void addToCart(String productId, String title, String priceEach, String price, String quantity) {
        itemId++;

        EasyDB easyDB = EasyDB.init(context,"ITEM_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id", new String[]{"text","unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price_Each", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text","not null"}))
                .doneTableColumn();

        Boolean b = easyDB.addData("Item_Id",itemId)
                .addData("Item_PID",productId)
                .addData("Item_Name",title)
                .addData("Item_Price_Each",priceEach)
                .addData("Item_Price",price)
                .addData("Item_Quantity",quantity)
                .doneDataAdding();

        Toast.makeText(context, "Added to cart...", Toast.LENGTH_SHORT).show();

        //update cart count
        //((ShopDetailsActivity)context).cartCount();

    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new FilterProductUser(this,filterList);
        }
        return filter;
    }

    class HolderProductUser extends RecyclerView.ViewHolder{

        //uid views
        private ImageView productIconIv;
        private TextView discountedNoteTv,titleTv,descriptionTv,addToCartTv,discountedPriceTv,originalPriceTv, totalStockTv;

        public HolderProductUser(@NonNull View itemView) {
            super(itemView);

            //init ui views
            productIconIv = itemView.findViewById(R.id.productIconIv);
            discountedNoteTv = itemView.findViewById(R.id.discountedNoteTv);
            titleTv = itemView.findViewById(R.id.titleTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
            addToCartTv = itemView.findViewById(R.id.addToCartTv);
            discountedPriceTv = itemView.findViewById(R.id.discountedPriceTv);
            originalPriceTv = itemView.findViewById(R.id.originalPriceTv);

        }
    }
}
