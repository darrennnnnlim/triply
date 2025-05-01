export interface PriceThresholdDialogData {
    thresholdPrice: number;
    conceptType: string; // 'FLIGHT' or 'HOTEL'
    conceptId: number; // ID of the flight or hotel
    userId: number; // ID of the user setting the threshold
    startDate: string; // Start date for the threshold
    endDate: string; // End date for the threshold
}