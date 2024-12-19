import { Component, OnInit } from '@angular/core';
import { Theme } from '../../model/theme.model';
import { ThemeService } from '../../services/theme.service';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.scss'
})
export class SettingsComponent implements OnInit {

  themes: Theme[] = [
    { val: '', name: 'Select theme' },
    { val: 'dark', name: 'Dark' },
    { val: 'light', name: 'Light' }
  ]
  activeTheme = localStorage.getItem('theme') || '';

  constructor(private themeService: ThemeService) { }

  ngOnInit(): void {}

  changeTheme($event: any) {
    const theme = $event.target.value;
    this.themeService.setTheme(theme);
  }

}
