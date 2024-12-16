import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Page } from '../../model/page.model';

@Component({
  selector: 'app-paginate',
  templateUrl: './paginate.component.html',
  styleUrl: './paginate.component.scss'
})
export class PaginateComponent implements OnInit {
  
  @Input() entity: Page<any>;
  @Output() currentPageEvent = new EventEmitter<number>();
  
  current: number = 0;
  total: number = 0;
  pages: number[] = [];

  ngOnInit(): void {
    this.total = this.entity.totalPages;
    this.setPage(0);
  }

  setPage(page: number): void {
    if (this.current !== page) {
      this.currentPageEvent.emit(page);
    }
    this.current = page;
    this.updatePages();
  }

  onNext(): void {
    if (this.current >= this.total - 1) {
      return;
    }
    this.setPage(this.current + 1);
  }

  onPrev(): void {
    if (this.current <= 0) {
      return;
    }
    this.setPage(this.current - 1);
  }

  updatePages(): void {
    if (this.total <= 3) {
      this.pages = Array.from({ length: this.total }, (_, i) => i);
      return;
    }

    if (this.current >= this.total - 1) {
      this.pages = [this.current - 2, this.current - 1, this.current];
    } else if (this.current <= 0) {
      this.pages = [this.current, this.current + 1, this.current + 2];
    } else {
      this.pages = [this.current - 1, this.current, this.current + 1];
    }

    this.pages = this.pages.filter(page => page >= 0 && page < this.total);
  }

}
