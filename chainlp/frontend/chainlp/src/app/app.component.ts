import { Component, HostListener } from '@angular/core';
import { ThemeService } from './services/theme.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'chaintest';

  constructor(private themeService: ThemeService) {
    const theme = themeService.getTheme();
    if (theme) {
      themeService.setTheme(theme);
    }
  }

  @HostListener('document:keypress', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent) { 
    if (event.key === 'l') {
      this.themeService.getTheme() === 'light' 
        ? this.themeService.setTheme('dark') : this.themeService.setTheme('light');
    }
  }

}
