package com.labactivity.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.labactivity.expensemanager.databinding.ActivityMain2Binding;
import com.labactivity.expensemanager.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity implements OnItemsClick{
    @NonNull
    ActivityMain2Binding binding;
    ActivityMainBinding binding2;
    private FirebaseAuth mAuth;
    private  ExpensesAdapter expensesAdapter;
    Intent intent;
    private long income=0, expense=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        expensesAdapter=new ExpensesAdapter(this, this);
        binding.recycler.setAdapter(expensesAdapter);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));

        intent = new Intent(MainActivity2.this, AddExpenseActivity.class);

        binding.addIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("type", "Income");
                startActivity(intent);

            }
        });
        binding.addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("type", "Expense");
                startActivity(intent);

            }
        });
    }


    @Override
    public void onStart() {

        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        income=0;expense=0;
        getData();
    }

    private void getData() {
        FirebaseFirestore.getInstance().collection("expenses").whereEqualTo("uid",FirebaseAuth.getInstance().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        expensesAdapter.clear();
                        List<DocumentSnapshot> dslist=queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot ds:dslist){
                            ExpenseModel expenseModel=ds.toObject(ExpenseModel.class);
                            if(expenseModel.getType().equals("Income")){
                                income+=expenseModel.getAmount();
                            }else{
                                expense+=expenseModel.getAmount();
                            }
                            expensesAdapter.add(expenseModel);
                        }
                        setUpGraph();
                    }
                });
    }

    private void setUpGraph() {
        List<PieEntry> pieEntryList = new ArrayList<>();
        List<Integer> colorList = new ArrayList<>();
        if (income != 0) {
            pieEntryList.add(new PieEntry(income, "Income"));
            colorList.add(getResources().getColor(R.color.teal_700));
        }
        if (expense != 0) {
            pieEntryList.add(new PieEntry(expense, "Expense                              Remaining Balance"));
            colorList.add(getResources().getColor(R.color.red));
        }
        PieDataSet pieDataSet=new PieDataSet(pieEntryList, String.valueOf(income-expense));
        pieDataSet.setColors(colorList);
        PieData pieDat=new PieData(pieDataSet);

        binding.pieChart.setData(pieDat);
        binding.pieChart.invalidate();
    }

    @Override
    public void onClick(ExpenseModel expenseModel) {
        intent.putExtra("model",expenseModel);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            // Handle logout
            FirebaseAuth.getInstance().signOut();
            // Redirect to login or another activity
            Intent intent = new Intent(MainActivity2.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close the current activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}