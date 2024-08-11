package com.labactivity.expensemanager;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.MyViewHolder> {
    private Context context;
    private OnItemsClick onItemsClick;
    private List<ExpenseModel> expenseModelList;

    public ExpensesAdapter(Context context, OnItemsClick onItemsClick) {
        this.context = context;
        expenseModelList=new ArrayList<>();
        this.onItemsClick=onItemsClick;
    }
    public void add(ExpenseModel expenseModel){
        expenseModelList.add(expenseModel);
        notifyDataSetChanged();
    }
    public void clear() {
        expenseModelList.clear();
        notifyDataSetChanged(); // Notify the adapter after clearing the list
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ExpenseModel expenseModel = expenseModelList.get(holder.getAdapterPosition());

        // Bind the data to the ViewHolder
        holder.note.setText(expenseModel.getNote());
        holder.category.setText(expenseModel.getCategory());
        holder.amount.setText(String.valueOf(expenseModel.getAmount()));
        holder.date.setText(expenseModel.getFormattedDate());

        // Set up click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the clicked ExpenseModel from the list based on the adapter position
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    ExpenseModel clickedExpenseModel = expenseModelList.get(adapterPosition);

                    // Start AddExpenseActivity with the clicked ExpenseModel
                    Intent intent = new Intent(context, AddExpenseActivity.class);
                    intent.putExtra("type", "Expense");
                    intent.putExtra("model", clickedExpenseModel);
                    context.startActivity(intent);
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return expenseModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView note,category,amount,date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            note=itemView.findViewById(R.id.note);
            category=itemView.findViewById(R.id.category);
            amount=itemView.findViewById(R.id.amount);
            date=itemView.findViewById(R.id.date);
        }
    }
}