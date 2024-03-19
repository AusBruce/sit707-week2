package com.example.myconvertunit;
import androidx.core.graphics.Insets;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.BreakIterator;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupEdgeToEdgeUI();

        Spinner spinnerUnitCategory = findViewById(R.id.spinner_conversion_type);
        Spinner spinnerSourceUnit = findViewById(R.id.spinner_source_unit);
        Spinner spinnerDestinationUnit = findViewById(R.id.spinner_destination_unit);
        EditText editTextValue = findViewById(R.id.editText_value);
        TextView textViewResult = findViewById(R.id.textView_result);
        Button buttonConvert = findViewById(R.id.button_convert);
//        textViewResult = findViewById(R.id.textView_result);

        setupSpinners(spinnerUnitCategory, spinnerSourceUnit, spinnerDestinationUnit);

        buttonConvert.setOnClickListener(v -> performConversion(spinnerUnitCategory, spinnerSourceUnit, spinnerDestinationUnit, editTextValue, textViewResult));
    }

    private void setupEdgeToEdgeUI() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupSpinners(Spinner spinnerUnitCategory, Spinner spinnerSourceUnit, Spinner spinnerDestinationUnit) {
        ArrayAdapter<CharSequence> adapterCategory = ArrayAdapter.createFromResource(this,
                R.array.unit_categories, android.R.layout.simple_spinner_item);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnitCategory.setAdapter(adapterCategory);

        spinnerUnitCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateSpinners(spinnerSourceUnit, spinnerDestinationUnit, getArrayIdForPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private int getArrayIdForPosition(int position) {
        switch (position) {
            case 0: return R.array.length_units;
            case 1: return R.array.weight_units;
            case 2: return R.array.temperature_units;
            default: throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    private void updateSpinners(Spinner source, Spinner destination, int unitsArrayId) {
        ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(this,
                unitsArrayId, android.R.layout.simple_spinner_item);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        source.setAdapter(unitAdapter);
        destination.setAdapter(unitAdapter);
    }

    private void performConversion(Spinner spinnerUnitCategory, Spinner spinnerSourceUnit, Spinner spinnerDestinationUnit, EditText editTextValue, TextView textViewResult) {
        String sourceUnit = spinnerSourceUnit.getSelectedItem().toString();
        String destUnit = spinnerDestinationUnit.getSelectedItem().toString();
        String inputValue = editTextValue.getText().toString();
        double value;
        if (inputValue.isEmpty()) {
            textViewResult.setText("Please enter a value");
            return;
        }

        // Try to parse the input value as a double
//        double value;
        try {
            value = Double.parseDouble(inputValue);
        } catch (NumberFormatException e) {
            textViewResult.setText("Invalid input. Please enter a numeric value.");
            return;
        }
        try {
            value = Double.parseDouble(editTextValue.getText().toString());
        } catch (NumberFormatException e) {
            textViewResult.setText(R.string.invalid_input);
            return;
        }

        double result = convertUnits(spinnerUnitCategory.getSelectedItem().toString(), sourceUnit, destUnit, value);
        textViewResult.setText(String.format(Locale.getDefault(), "%.2f", result));
    }



    private double convertUnits(String category, String sourceUnit, String destUnit, double value) {
        // Check if source and destination units are the same


        switch (category) {
            case "Length":
                return convertLength(sourceUnit, destUnit, value);
            case "Weight":
                return convertWeight(sourceUnit, destUnit, value);
            case "Temperature":
                return convertTemperature(sourceUnit, destUnit, value);
            default:
                throw new IllegalArgumentException("Unsupported conversion category: " + category);
        }
    }
    private double convertLength(String sourceUnit, String destUnit, double value) {
        // First, convert source unit to meters as a base unit
        double valueInMeters;
        switch (sourceUnit) {
            case "Inches": valueInMeters = value * 2.54 / 100; break;
            case "Feet": valueInMeters = value * 30.48 / 100; break;
            case "Yards": valueInMeters = value * 91.44 / 100; break;
            case "Miles": valueInMeters = value * 1609.34; break;
            case "Centimeters": valueInMeters = value / 100; break;
            case "Meters": valueInMeters = value; break;
            case "Kilometers": valueInMeters = value * 1000; break;
            default: throw new IllegalArgumentException("Unsupported length unit: " + sourceUnit);
        }

        // Then, convert from meters to the destination unit
        switch (destUnit) {
            case "Inches": return valueInMeters / (2.54 / 100);
            case "Feet": return valueInMeters / (30.48 / 100);
            case "Yards": return valueInMeters / (91.44 / 100);
            case "Miles": return valueInMeters / 1609.34;
            case "Centimeters": return valueInMeters * 100;
            case "Meters": return valueInMeters;
            case "Kilometers": return valueInMeters / 1000;
            default: throw new IllegalArgumentException("Unsupported length unit: " + destUnit);
        }
    }

    private double convertWeight(String sourceUnit, String destUnit, double value) {
        double valueInKilograms;
        switch (sourceUnit) {
            case "Ounces":
                valueInKilograms = value * 28.3495 / 1000; // Convert ounces to kilograms
                break;
            case "Pounds":
                valueInKilograms = value * 0.453592; // Convert pounds to kilograms
                break;
            case "Tons":
                valueInKilograms = value * 907.185; // Convert tons to kilograms
                break;
            case "Grams":
                valueInKilograms = value / 1000; // Convert grams to kilograms
                break;
            case "Kilograms":
                valueInKilograms = value; // Already in kilograms
                break;
            default:
                throw new IllegalArgumentException("Unsupported weight unit: " + sourceUnit);
        }

        switch (destUnit) {
            case "Ounces":
                return valueInKilograms * 1000 / 28.3495; // Convert kilograms to ounces
            case "Pounds":
                return valueInKilograms / 0.453592; // Convert kilograms to pounds
            case "Tons":
                return valueInKilograms / 907.185; // Convert kilograms to tons
            case "Grams":
                return valueInKilograms * 1000; // Convert kilograms to grams
            case "Kilograms":
                return valueInKilograms; // Already in kilograms
            default:
                throw new IllegalArgumentException("Unsupported weight unit: " + destUnit);
        }
    }



    private double convertTemperature(String sourceUnit, String destUnit, double value) {
        switch (sourceUnit) {
            case "Celsius":
                if (destUnit.equals("Fahrenheit")) {
                    return (value * 1.8) + 32; // Celsius to Fahrenheit
                } else if (destUnit.equals("Kelvin")) {
                    return value + 273.15; // Celsius to Kelvin
                }
                break;
            case "Fahrenheit":
                if (destUnit.equals("Celsius")) {
                    return (value - 32) / 1.8; // Fahrenheit to Celsius
                } else if (destUnit.equals("Kelvin")) {
                    return ((value - 32) / 1.8) + 273.15; // Fahrenheit to Kelvin indirectly
                }
                break;
            case "Kelvin":
                if (destUnit.equals("Celsius")) {
                    return value - 273.15; // Kelvin to Celsius
                } else if (destUnit.equals("Fahrenheit")) {
                    return ((value - 273.15) * 1.8) + 32; // Kelvin to Fahrenheit indirectly
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported temperature unit: " + sourceUnit);
        }
        return value; // Return the input value if source and destination units are the same
    }




}


