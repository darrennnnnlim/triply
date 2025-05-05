import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-popup',
  imports: [CommonModule],
  templateUrl: './popup.component.html',
  styleUrl: './popup.component.css'
})
export class PopupComponent {
  @Input() message: string = '';
  @Output() close = new EventEmitter<void>();

  onClose(): void{
    console.log('Closing popup...');
    this.close.emit(); 
  }
}
