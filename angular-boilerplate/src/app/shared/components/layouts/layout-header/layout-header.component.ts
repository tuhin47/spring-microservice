// Angular modules
import { Component }   from '@angular/core';
import { OnInit }      from '@angular/core';
import { Router }      from '@angular/router';

// Internal modules
import { environment } from '@env/environment';
import {StorageHelper} from "@helpers/storage.helper";

@Component({
  selector    : 'app-layout-header',
  templateUrl : './layout-header.component.html',
  styleUrls   : ['./layout-header.component.scss']
})
export class LayoutHeaderComponent implements OnInit
{
  public appName         : string  = environment.appName;
  public isMenuCollapsed : boolean = true;

  constructor
  (
    private router : Router,
  )
  {

  }

  public ngOnInit() : void
  {

  }

  // -------------------------------------------------------------------------------
  // NOTE Init ---------------------------------------------------------------------
  // -------------------------------------------------------------------------------

  // -------------------------------------------------------------------------------
  // NOTE Actions ------------------------------------------------------------------
  // -------------------------------------------------------------------------------

  public async onClickLogout() : Promise<void>
  {
    // NOTE Redirect to login
    StorageHelper.removeToken();
    this.router.navigate(['/auth/login']);
  }

  // -------------------------------------------------------------------------------
  // NOTE Computed props -----------------------------------------------------------
  // -------------------------------------------------------------------------------

  // -------------------------------------------------------------------------------
  // NOTE Helpers ------------------------------------------------------------------
  // -------------------------------------------------------------------------------

  // -------------------------------------------------------------------------------
  // NOTE Requests -----------------------------------------------------------------
  // -------------------------------------------------------------------------------

  // -------------------------------------------------------------------------------
  // NOTE Subscriptions ------------------------------------------------------------
  // -------------------------------------------------------------------------------

}
