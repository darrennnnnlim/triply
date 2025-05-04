import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog'; // Import MatDialogModule
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule } from '@angular/forms'; // Import FormsModule
// Interface for the data structure to be handled
export interface PriceThresholdDialogData {
    thresholdPrice: number;
    conceptType: string; // 'FLIGHT' or 'HOTEL'
    conceptId: number; // ID of the flight or hotel
    userId: number; // ID of the user setting the threshold
    startDate: string; // Start date for the threshold
    endDate: string; // End date for the threshold
}

@Component({
  selector: 'app-price-threshold-dialog',
  templateUrl: './price-threshold-dialog.component.html',
  styleUrls: ['./price-threshold-dialog.component.css'],
  standalone: true, // Make it standalone
  imports: [ // Import necessary modules for standalone
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    FormsModule, // Add FormsModule here
    MatDialogModule // Import MatDialogModule for dialog elements
  ]
})
export class PriceThresholdDialogComponent {
  threshold: number;

  constructor(
    public dialogRef: MatDialogRef<PriceThresholdDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any // You can pass data to the dialog if needed
  ) {
    // Initialize threshold, potentially from passed data or a default
    this.threshold = data?.initialThreshold || 0;
  }

  onCancel(): void {
    this.dialogRef.close(); // Close without returning data
  }

  onSet(): void {
    // You might want validation here
    this.dialogRef.close(this.threshold); // Close and return the threshold value
  }
}