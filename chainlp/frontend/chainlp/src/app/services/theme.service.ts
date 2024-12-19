import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {

  constructor() { }

  getTheme() {
    return localStorage.getItem('theme') || '';
  }

  setTheme(theme: string) {
    localStorage.setItem('theme', theme);
    if (theme) {
      document.body.setAttribute('data-bs-theme', theme);
    }
  }

}
