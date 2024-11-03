package com.example.fittarget;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class SummaryActivity extends AppCompatActivity {

    private PieChart muscleGroupPieChart;
    private Spinner bodyPartSpinner;
    private Button generateGraphButton;
    private LineChart weightLineChart;

    // Map to store date and weight data for each body part
    private Map<String, List<Entry>> bodyPartWeightData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        muscleGroupPieChart = findViewById(R.id.muscleGroupPieChart);
        bodyPartSpinner = findViewById(R.id.bodyPartSpinner);
        generateGraphButton = findViewById(R.id.generateGraphButton);
        weightLineChart = findViewById(R.id.weightLineChart);

        // Populate the body part spinner with options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.muscle_groups, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bodyPartSpinner.setAdapter(adapter);

        // Retrieve and populate data for each body part
        populateBodyPartWeightData();

        // Set up the generate button click listener
        generateGraphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedBodyPart = bodyPartSpinner.getSelectedItem().toString();
                generateLineChartForBodyPart(selectedBodyPart);
            }
        });

        // Generate the pie chart for workout frequency
        generatePieChart();
    }

    private void populateBodyPartWeightData() {
        // Retrieve serialized data for each muscle group from intent extras
        bodyPartWeightData.put("Legs", (ArrayList<Entry>) getIntent().getSerializableExtra("data_Legs"));
        bodyPartWeightData.put("Arms", (ArrayList<Entry>) getIntent().getSerializableExtra("data_Arms"));
        bodyPartWeightData.put("Shoulder", (ArrayList<Entry>) getIntent().getSerializableExtra("data_Shoulder"));
        bodyPartWeightData.put("Back", (ArrayList<Entry>) getIntent().getSerializableExtra("data_Back"));
        bodyPartWeightData.put("Chest", (ArrayList<Entry>) getIntent().getSerializableExtra("data_Chest"));
    }

    private void generateLineChartForBodyPart(String bodyPart) {
        List<Entry> dataEntries = bodyPartWeightData.get(bodyPart);
        List<String> xAxisLabels = getIntent().getStringArrayListExtra("dateLabels_" + bodyPart); // Retrieve date labels

        if (dataEntries == null || dataEntries.isEmpty() || xAxisLabels == null || xAxisLabels.isEmpty()) {
            // No data available for the selected body part
            weightLineChart.clear();
            weightLineChart.setNoDataText("No data available for " + bodyPart);
            return;
        }

        // Set up the data set and line data
        LineDataSet lineDataSet = new LineDataSet(dataEntries, bodyPart + " - Weight Lifted Over Time");
        lineDataSet.setCircleRadius(5f);
        lineDataSet.setLineWidth(2f);

        LineData lineData = new LineData(lineDataSet);
        weightLineChart.setData(lineData);

        // Set custom x-axis labels for months
        XAxis xAxis = weightLineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // Set description for the line chart
        Description description = new Description();
        description.setText("Weight Lifted");
        weightLineChart.setDescription(description);

        weightLineChart.invalidate(); // Refresh the chart
    }


    private void generatePieChart() {
        // Retrieve the data for workout frequency for each muscle group
        int frequencyLegs = getIntent().getIntExtra("frequency_Legs", 0);
        int frequencyArms = getIntent().getIntExtra("frequency_Arms", 0);
        int frequencyShoulder = getIntent().getIntExtra("frequency_Shoulder", 0);
        int frequencyBack = getIntent().getIntExtra("frequency_Back", 0);
        int frequencyChest = getIntent().getIntExtra("frequency_Chest", 0);

        // Prepare data entries for the pie chart based on workout frequency
        List<PieEntry> entries = new ArrayList<>();
        if (frequencyLegs > 0) entries.add(new PieEntry(frequencyLegs, "Legs"));
        if (frequencyArms > 0) entries.add(new PieEntry(frequencyArms, "Arms"));
        if (frequencyShoulder > 0) entries.add(new PieEntry(frequencyShoulder, "Shoulder"));
        if (frequencyBack > 0) entries.add(new PieEntry(frequencyBack, "Back"));
        if (frequencyChest > 0) entries.add(new PieEntry(frequencyChest, "Chest"));

        // Only set data if there are entries available
        if (!entries.isEmpty()) {
            PieDataSet dataSet = new PieDataSet(entries, "");
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);

            // Define distinct colors for each body part
            List<Integer> colors = new ArrayList<>();
            colors.add(Color.parseColor("#FF6347")); // Legs - Tomato color
            colors.add(Color.parseColor("#4682B4")); // Arms - Steel Blue color
            colors.add(Color.parseColor("#32CD32")); // Shoulder - Lime Green color
            colors.add(Color.parseColor("#FFD700")); // Back - Gold color
            colors.add(Color.parseColor("#FF69B4")); // Chest - Hot Pink color
            dataSet.setColors(colors);

            PieData pieData = new PieData(dataSet);
            muscleGroupPieChart.setData(pieData);
            muscleGroupPieChart.setUsePercentValues(true);
            muscleGroupPieChart.setEntryLabelTextSize(12f);

            // Set description for the pie chart
            Description description = new Description();
            description.setText("Workout Frequency by Muscle Group");
            muscleGroupPieChart.setDescription(description);

            muscleGroupPieChart.invalidate(); // Refresh the chart
        } else {
            muscleGroupPieChart.setNoDataText("No workout data available.");
        }
    }



}
