package th.ac.tu.siit.its333.lab4exercise1;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    CourseDBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = new CourseDBHelper(this);

        recal();

    }

    public void recal()
    {


        SQLiteDatabase db = helper.getReadableDatabase();

        // cr is column 0, gp is column 2
        //Cursor cursor = db.rawQuery("SELECT * FROM course;", null);

        Cursor cursor = db.rawQuery("SELECT SUM(credit) cr, SUM(value*credit) gp FROM course;", null);

            cursor.moveToFirst();
            int totalCredit = cursor.getInt(0);



                TextView tvGP = (TextView) findViewById(R.id.tvGP);
                tvGP.setText("0.0");

                TextView tvCredits = (TextView) findViewById(R.id.tvCR);
                tvCredits.setText("0");

                TextView tvGPA = (TextView) findViewById(R.id.tvGPA);
                tvGPA.setText("0.0");

            if(totalCredit > 0)
            {
                double totalGP = cursor.getDouble(1);
                double totalGPA = totalGP/totalCredit;

                System.out.println("get credit: " + totalCredit);

                tvGP.setText(Double.toString(totalGP));

                tvCredits.setText(Integer.toString(totalCredit));

                tvGPA.setText((String.format("%.2f",totalGPA)));
            }




    }

    @Override
    protected void onResume() {
        super.onResume();
        // This method is called when this activity is put foreground.

        System.out.println("ON RESUME");

        recal();

    }

    public void buttonClicked(View v) {
        int id = v.getId();
        Intent i;

        switch(id) {
            case R.id.btAdd:
                i = new Intent(this, AddCourseActivity.class);
                startActivityForResult(i, 88);
                break;

            case R.id.btShow:
                i = new Intent(this, ListCourseActivity.class);
                startActivity(i);
                break;

            case R.id.btReset:
                deleteRecord();
                break;
        }
    }

    public void deleteRecord()
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        int n_rows = db.delete("course", null, null);

        recal();

        TextView tvGPA = (TextView) findViewById(R.id.tvGPA);
        tvGPA.setText("0.0");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 88) {
            if (resultCode == RESULT_OK) {
                String code = data.getStringExtra("code");
                int credit = data.getIntExtra("credit", 0);
                String grade = data.getStringExtra("grade");

                // Coonect to database, table 'course'
                helper = new CourseDBHelper(this);
                SQLiteDatabase db = helper.getWritableDatabase();

                // Create the content to insert
                ContentValues content = new ContentValues();
                content.put("code", code);
                content.put("credit", credit);
                content.put("grade", grade);
                content.put("value", gradeToValue(grade));
                long new_id = db.insert("course", null, content);

                System.out.println("new id:" + new_id);

                //recal();
            }
        }

        Log.d("course", "onActivityResult");
    }

    double gradeToValue(String g) {
        if (g.equals("A"))
            return 4.0;
        else if (g.equals("B+"))
            return 3.5;
        else if (g.equals("B"))
            return 3.0;
        else if (g.equals("C+"))
            return 2.5;
        else if (g.equals("C"))
            return 2.0;
        else if (g.equals("D+"))
            return 1.5;
        else if (g.equals("D"))
            return 1.0;
        else
            return 0.0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

//SELECT SUM(credit) cr,
// SUM(value*credit) gp FROM course;