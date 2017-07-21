
package com.sheez.admin.mymoneytracker;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    SQLiteHelper database = new SQLiteHelper(this);

    private List<String> categories;
    private ArrayAdapter<String> categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        categories = new ArrayList<>();
        categoryAdapter = new ArrayAdapter<>(this, R.layout.category, categories);

        GridView view = (GridView)findViewById(R.id.categoriesGridView);
        view.setAdapter(categoryAdapter);
    }

    @Override
    public void onResume() {
        refreshViews();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.addCategoryOption:
                Intent intent = new Intent(this, CategoryActivity.class);
                startActivityForResult(intent, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshViews()
    {
        int total_amount = database.getTotal();
        TextView total_expenses_label = (TextView)findViewById(R.id.totalTextView);
        total_expenses_label.setText(String.format("%d.%02d $", total_amount/100, (total_amount - ((total_amount/100)*100))));
        categories.clear();
        categories.addAll(database.getCategoriesOrderedByCount());
        categoryAdapter.notifyDataSetChanged();
    }

    public void ShowChartOverview(View view)
    {
        Intent intent = new Intent(view.getContext(), OverviewActivity.class);
        startActivityForResult(intent, 0);
    }

    public void ShowListOverview(View view)
    {
        Intent intent = new Intent(view.getContext(), ListActivity.class);
        startActivityForResult(intent, 0);
    }

    public void AddExpense(View view)
    {
        Intent intent = new Intent(view.getContext(), ExpenseActivity.class);
        intent.putExtra("category", ((Button)view).getText().toString());
        startActivityForResult(intent, 0);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK)
            return;
        if (requestCode == 0) // then a transaction is added
        {
            database.addTransaction((Transaction)data.getExtras().get("transaction")); }
        else if(requestCode == 1) // then category is added
        {
            String category = data.getExtras().getString("category");
            if(!category.isEmpty())
            database.addCategory(category); }
    }

}

