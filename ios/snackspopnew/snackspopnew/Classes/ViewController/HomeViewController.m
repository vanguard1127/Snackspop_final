//
//  HomeViewController.m
//  snackspopnew
//
//  Created by Admin on 2019-04-05.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "HomeViewController.h"
#import "SignInViewController.h"
#import "../NavigationView/LoginNavView.h"
#import "../NavigationView/NormalNavView.h"
#import <ECDrawerLayout/ECDrawerLayout.h>
@import Firebase;

@interface HomeViewController () <ECDrawerLayoutDelegate>
@property (strong, nonatomic) ECDrawerLayout* drawerLayout;
@property (strong, nonatomic) LoginNavView * drawerView;
@property (strong, nonatomic) NormalNavView * n_drawerView;
@property (assign, nonatomic) NSInteger mPageIndex;

@end

@implementation HomeViewController
-(void)setTitle:(NSString *)title
{
    [self.mLblTitle setText:title];
}

-(void)setSubView:(NSString*)viewNibName
{
   UIView *view1 = [[[NSBundle mainBundle] loadNibNamed:viewNibName owner:self options:nil] objectAtIndex:0];
    
    NSArray<UIView*> *subViews = [_mSubView subviews];
    for(UIView* subView in subViews) {
        [subView removeFromSuperview];
    }
    
    CGRect rect = [_mSubView frame];
    rect.origin.y = 0;
    rect.origin.x = 0;
    [view1 setFrame:rect];
    
    [_mSubView addSubview:view1];
    
    
}
- (LoginNavView*) drawerView
{
    if (!_drawerView) {
        
        _drawerView = [[[NSBundle mainBundle] loadNibNamed:@"login_navigation_drawer" owner:self options:nil] objectAtIndex:0];
        
        __weak HomeViewController *weekSelf = self;
        _drawerView.menuCallback = ^(NSInteger index, NSString* title) {
            [weekSelf.drawerLayout toggle];
            
            //self._mPageIndex = index;
            switch (index) {
                case 0:
                    [weekSelf setSubView:@"HomeView"];
                    [weekSelf setTitle:@"Snackspop"];
                    break;
                case 1:
                    [weekSelf setSubView:@"MyProfileView"];
                    [weekSelf setTitle:@"My Profile"];
                    break;
                case 2:
                    [weekSelf setSubView:@"MyProductView"];
                    [weekSelf setTitle:@"My Products"];
                    break;
                case 3:
                    [weekSelf setSubView:@"MyChatView"];
                    [weekSelf setTitle:@"My Chats"];
                    break;
                case 4:
                    [weekSelf setSubView:@"AboutUsView"];
                    [weekSelf setTitle:@"About Us"];
                    break;
                case 5:
                    //TODO
                {
                    [AppUtils setLogOut];
                    HomeViewController *homeView = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"HomeViewId"];
                    
                    [weekSelf presentViewController:homeView animated:YES completion:nil];
                }
                    break;
                default:
                    break;
            }
        };
    }
    return _drawerView;
}
- (NormalNavView*) n_drawerView
{
    if (!_n_drawerView) {
        
        _n_drawerView = [[[NSBundle mainBundle] loadNibNamed:@"normal_navigation_drawer" owner:self options:nil] objectAtIndex:0];
        
        __weak HomeViewController *weekSelf = self;
        _n_drawerView.menuCallback = ^(NSInteger index, NSString* title) {
            [weekSelf.drawerLayout toggle];
            
            //self._mPageIndex = index;
            switch (index) {
                case 0:
                    [weekSelf setSubView:@"HomeView"];
                    [weekSelf setTitle:@"Snackspop"];
                    break;
                case 1:
                    [weekSelf setSubView:@"AboutUsView"];
                    [weekSelf setTitle:@"About Us"];
                    break;
                case 2:
                {
                    SignInViewController *signInViewController = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"SignInViewId"];
                    [weekSelf presentViewController:signInViewController animated:YES completion:nil];
                    
                }
                    break;
                default:
                    break;
            }
        };
    }
    return _n_drawerView;
}
-(void) checkLocationServicesAndStartUpdates
{
    locationManager = [[CLLocationManager alloc] init];
    locationManager.distanceFilter = kCLDistanceFilterNone;
    locationManager.delegate = self;
    locationManager.desiredAccuracy = kCLLocationAccuracyBest;
    
    if ([locationManager respondsToSelector:@selector(requestWhenInUseAuthorization)])
    {
        [locationManager requestWhenInUseAuthorization];
    }
    
    //Checking authorization status
    if (![CLLocationManager locationServicesEnabled] && [CLLocationManager authorizationStatus] == kCLAuthorizationStatusDenied)
    {
        UIAlertController *alertController = [UIAlertController alertControllerWithTitle:@"Location Services Disabled!" message:@"Please enable Location Based Services for better results! We promise to keep your location private" preferredStyle:UIAlertControllerStyleAlert];
        [alertController addAction:[UIAlertAction actionWithTitle:@"Settings" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            if (![CLLocationManager locationServicesEnabled])
            {

                UIApplication *application = [UIApplication sharedApplication];
                NSURL *settingURL = [NSURL URLWithString:UIApplicationOpenSettingsURLString];
                if ([[UIApplication sharedApplication] canOpenURL:settingURL])
                {
                    if (@available(iOS 10.0, *)) {
                        [application openURL:settingURL options:@{} completionHandler:nil];
                    }
                    else
                    {
                        [application openURL:settingURL];
                        
                    }
                }

            }
            else
            {
                UIApplication *application = [UIApplication sharedApplication];
                NSURL *URL = [NSURL URLWithString:UIApplicationOpenSettingsURLString];
                [application openURL:URL options:@{} completionHandler:nil];
            }
        }]];
        [alertController addAction:[UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            
        }]];
        [[AppUtils currentTopViewController] presentViewController:alertController animated:YES completion:^{
            
        }];
  
        return;
    }
    else
    {
        //Location Services Enabled, let's start location updates
        [locationManager startUpdatingLocation];
    }
    
    
}

-(void)startUpdateDeviceToken:(NSString *)token
{
    [AppUtils addActivityIndicator:self.view];
    NSString *serverurl = [BASE_URL stringByAppendingString:USER_UPDATE ];
    
    
    
    NSNumber *number = [NSNumber numberWithInteger:1];
    NSDictionary *params = @{@"device_type":number , @"device_id":token};
    

    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [manager.requestSerializer setValue:[AppUtils getUserToken] forHTTPHeaderField:@"x-access-token"];
    
    [manager PUT:serverurl
      parameters:params
         success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
             NSLog(@"Update Success!");
             NSLog(@"JSON: %@", responseObject);
             NSDictionary *user = [responseObject objectForKey:@"user"];
             [AppUtils saveUserAccountInfo:user];            
             [AppUtils removeActivityIndicator:self.view];
             
         } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
             NSLog(@"Error: %@", error);
             
             [AppUtils removeActivityIndicator:self.view];
             
             if (error.code == -1004)
             {
                 [AppUtils showMessageWithOk:@"Server down or issue with the internet connection. Please check your connection." withTitle:@"Alert"];
             }
             else if (error.code == -1005)
             {
                 [AppUtils showMessageWithOk:@"Server down or issue with the internet connection." withTitle:@"Alert"];
             } else {
                 NSError *jsonError;
                 NSData * data = error.userInfo[AFNetworkingOperationFailingURLResponseDataErrorKey];
                 
                 NSDictionary * json = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers  error:&jsonError];
                 [AppUtils showMessageWithOk:[json objectForKey:@"error"] withTitle:@"Alert"];
             }
             
         }];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    
    
    [[NSNotificationCenter defaultCenter]
     addObserver:self selector:@selector(triggerNotification:) name:@"NotificationMessageEvent" object:nil];
    
    
    [[NSNotificationCenter defaultCenter]
     addObserver:self selector:@selector(triggerUserUpdatedNotification:) name:@"UserInfoUpdated" object:nil];
    
    self.drawerLayout = [[ECDrawerLayout alloc] initWithParentView:self.view];
  
//    [self checkLocationServicesAndStartUpdates];
    
    if ([AppUtils isLoggedIn])
    {
        self.drawerLayout.contentView = self.drawerView;
        self.drawerView.userName = [AppUtils getUserName];
        self.drawerView.userImage = [AppUtils getUserImageUrl];

        [[FIRInstanceID instanceID] instanceIDWithHandler:^(FIRInstanceIDResult * _Nullable result,
                                                            NSError * _Nullable error) {
            if (error != nil) {
                NSLog(@"Error fetching remote instance ID: %@", error);
            } else {
                NSLog(@"Remote instance ID token: %@", result.token);
                [self startUpdateDeviceToken:result.token];
            }
        }];
        
        [AppUtils setAppRunning:1];
        


    }
    else
    {
        self.drawerLayout.contentView = self.n_drawerView;
        /*self.n_drawerView.userName = [AppUtils getUserName];
        self.n_drawerView.userImage = [AppUtils getUserImageUrl];*/
    }

    self.drawerLayout.delegate = self;
    [self.view addSubview:self.drawerLayout];

    [self setSubView:@"HomeView"];
    
}
-(void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    if ([AppUtils isLoggedIn])
    {
        
        
        NSInteger isPush = [AppUtils isChatPush];
        if (isPush == 1)
        {
            ChatItemModel *m_model = [AppUtils getNotifiUserData];
            UIViewController *currentTopVC = [AppUtils currentTopViewController];
            ChattingViewController *view = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"ChattingViewId"];
            view.model = m_model;
            
            [currentTopVC presentViewController:view animated:true completion:nil];

            
            [AppUtils setChatPush:0];
        }
    }
}
-(void)viewDidLayoutSubviews{

}

- (void) onLeftItemTouched: (UIBarButtonItem*) sender
{
    self.drawerLayout.openFromRight = NO;
    [self.drawerLayout openDrawer];
}

- (void) onRightItemTouched:(UIBarButtonItem*) sender
{
    self.drawerLayout.openFromRight = YES;
    [self.drawerLayout openDrawer];
}



#pragma mark - ECDrawerLayoutDelegate
- (void) drawerLayoutDidOpen
{
    NSLog(@"drawerLayout open");
}
- (void) drawerLayoutDidClose
{
    NSLog(@"drawerLayout close");
}
- (IBAction)onBtnMenuClick:(id)sender {
    self.drawerLayout.openFromRight = NO;
    [self.drawerLayout openDrawer];
}


- (void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray<CLLocation *> *)locations
{
    CLLocation *newLocation = [locations lastObject];
    CLLocation *oldLocation;
    if (locations.count > 1) {
        oldLocation = [locations objectAtIndex:locations.count-2];
    } else {
        oldLocation = nil;
    }
    NSLog(@"didUpdateToLocation %@ from %@", newLocation, oldLocation);

    [AppUtils setServiceLocation:newLocation];
    
    
    [[NSNotificationCenter defaultCenter] postNotificationName:@"UpdatedLocation" object:nil];
    
}

#pragma mark - Notification
-(void) triggerNotification:(NSNotification *) notification
{
    NSDictionary *dict = notification.userInfo;
    NSDictionary *message = [dict valueForKey:@"message"];
    if (message != nil) {
        // do stuff here with your message data
        NSInteger number = [[message objectForKey:@"type"] integerValue];
        if (number == 1)
        {
            [self checkLocationServicesAndStartUpdates];
        }
        NSLog(@"aaa");
    }
}

-(void) triggerUserUpdatedNotification:(NSNotification *) notification
{
    if ([AppUtils isLoggedIn])
    {
        self.drawerLayout.contentView = self.drawerView;
        self.drawerView.userName = [AppUtils getUserName];
        self.drawerView.userImage = [AppUtils getUserImageUrl];
    }
}
@end
