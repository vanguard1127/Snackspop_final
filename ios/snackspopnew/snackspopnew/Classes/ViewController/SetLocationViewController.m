//
//  SetLocationViewController.m
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "SetLocationViewController.h"

@interface SetLocationViewController ()

@end

@implementation SetLocationViewController
@synthesize mMapView;

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.mMapView.delegate = self;
    mMapView.showsUserLocation = NO;
    mMapView.mapType = MKMapTypeStandard;
   
    self.mAddressAppeared = 0;
    
    
	
}

-(void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    if (self.mAddressAppeared == 0)
    {
        self.mAddressAppeared = 1;
        NSString *addresss = [self showAddress];
        
        
        self.myCurrentPosition = [[CLLocation alloc] initWithLatitude:0
                                                            longitude:0];
        
        [self.mAddresContainerView setHidden:YES];
//        if ([AppUtils isLoggedIn])
//        {
//            [self.mAddresContainerView setHidden:NO];
//            self.myCurrentPosition = [AppUtils getUserLocation];
//            
//            if (self.myCurrentPosition.coordinate.latitude == 0)
//            {
//                
//                self.myCurrentPosition = [AppUtils getServiceLocation];
//                
////                if ([AppUtils isTextEmpty:addresss])
////                {
////                    [self showAddressController];
////                }
//            }
//        }
//        else{
            self.myCurrentPosition = [AppUtils getServiceLocation];
            
//        }
        
        [self toMyLocation];
    }
    
}
-(void)toMyLocation
{
    MKPointAnnotation *annotation = [[MKPointAnnotation alloc] init];
    [annotation setCoordinate:self.myCurrentPosition.coordinate];
    [annotation setTitle:@"MyLocation"]; //You can set the subtitle too
    [self.mMapView addAnnotation:annotation];
    
    CLLocationCoordinate2D center = self.myCurrentPosition.coordinate;
    //center.latitude -= self.mMapView.region.span.latitudeDelta * 0.40;
    [self.mMapView setCenterCoordinate:center animated:YES];
    
//    CLLocationCoordinate2D target = CLLocationCoordinate2DMake(self.myCurrentPosition.coordinate.latitude , self.myCurrentPosition.coordinate.longitude);
//    CLLocationCoordinate2D viewPoint = CLLocationCoordinate2DMake(self.myCurrentPosition.coordinate.latitude , self.myCurrentPosition.coordinate.longitude );
//    [mMapView setPitchEnabled:YES];
//    MKMapCamera *camera = [MKMapCamera cameraLookingAtCenterCoordinate:target
//                                                     fromEyeCoordinate:viewPoint
//                                                           eyeAltitude:500];
    
//    mMapView.camera = camera;
//
//    MKCoordinateSpan span = {.latitudeDelta =  0.01, .longitudeDelta =  0.01};
//    MKCoordinateRegion region = {target, span};
//    [mMapView setRegion:region animated:YES];
    


}
- (void)mapView:(MKMapView *)aMapView didUpdateUserLocation:(MKUserLocation *)aUserLocation {
//    NSLog(@"didUpdateUserLocation");
//    MKCoordinateRegion region;
//    MKCoordinateSpan span;
//    span.latitudeDelta = 0.005;
//    span.longitudeDelta = 0.005;
//    CLLocationCoordinate2D location;
//    location.latitude = aUserLocation.coordinate.latitude;
//    location.longitude = aUserLocation.coordinate.longitude;
//    region.span = span;
//    region.center = location;
//    [aMapView setRegion:region animated:YES];
}


- (IBAction)onBtnBack:(id)sender {
    [self dismissViewControllerAnimated:YES completion:NULL];
}

- (IBAction)onBtnSave:(id)sender {
    
    CLLocationCoordinate2D centre = [mMapView centerCoordinate];
    
    self.myCurrentPosition = [[CLLocation alloc] initWithLatitude:centre.latitude
                                                        longitude:centre.longitude];
    
    if ([AppUtils isLoggedIn])
        [self startUpdateLocation];
    else
    {
        [AppUtils setLocationLocation:self.myCurrentPosition];
        [self dismissViewControllerAnimated:YES completion: ^{
            if (self.mdismisCallBack != nil)
            {
                self.mdismisCallBack(1);
            }
        }];
    }
    NSLog(@"BtnSave");
}

- (IBAction)onBtnSetAddress:(id)sender {
    
    [self showAddressController];
    
}
-(NSString *)showAddress
{
    
    NSDictionary *userObject = [AppUtils getUserInfo];
    NSString *address_line1  = [AppUtils getStringFromDictionary:userObject objectForKey:@"address_line1"];
    NSString *address_line2 = [AppUtils getStringFromDictionary:userObject objectForKey:@"address_line2"];
    NSString *city = [AppUtils getStringFromDictionary:userObject objectForKey:@"city"];
    NSString *state = [AppUtils getStringFromDictionary:userObject objectForKey:@"county_state_province"];
    NSString *country = [AppUtils getStringFromDictionary:userObject objectForKey:@"country"];
    id mZipCode = [userObject objectForKey:@"zip_code"];
    NSString *zipcode = @"";
    if ((NSNull *)mZipCode != [NSNull null])
    {
        NSInteger mmzip = [mZipCode integerValue];

        zipcode = [NSString stringWithFormat:@"%ld",mmzip];
    }
    

    NSString *address = [[[[[[[[[[address_line1 stringByAppendingString:@","] stringByAppendingString:address_line2] stringByAppendingString:@","]  stringByAppendingString:city] stringByAppendingString:@","] stringByAppendingString:state] stringByAppendingString:@","] stringByAppendingString:country] stringByAppendingString:@","] stringByAppendingString:zipcode];
    
    if (address.length < 6)
    {
        address = @"";
    }
    self.mAddressView.text = address;
    return address;
}
-(void) showAddressController
{
    UIViewController *currentTopVC = [AppUtils currentTopViewController];
    SetAddressViewController *view = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"SetAddressViewId"];
    view.myLocation = self.myCurrentPosition;
    view.mdismisCallBack = ^(NSInteger param)
    {
        [self showAddress];
    };
    [currentTopVC presentViewController:view animated:true completion:nil];
}
- (IBAction)onBtnMyLocation:(id)sender {
    [self toMyLocation];
}

-(void)startUpdateLocation
{
    [AppUtils addActivityIndicator:self.view];
    NSString *serverurl = [BASE_URL stringByAppendingString:USER_UPDATE ];
    
    NSDictionary *params = [self makeRequestParams];
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
             
//             dispatch_async(dispatch_get_main_queue(), ^{
                 // Update the UI on the main thread.
                 [self dismissViewControllerAnimated:YES completion: ^{
                     if (self.mdismisCallBack != nil)
                     {
                         self.mdismisCallBack(1);
                     }
                 }];
//             });
             

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
-(NSDictionary *)makeRequestParams
{
    NSMutableDictionary *mutableDict = [[NSMutableDictionary alloc]init];
    
    NSNumber *lat = [NSNumber numberWithDouble:self.myCurrentPosition.coordinate.latitude];
    NSNumber *lng = [NSNumber numberWithDouble:self.myCurrentPosition.coordinate.longitude];
    
    [mutableDict setObject:lat forKey:@"geo_lat"];
   
    [mutableDict setObject:lng forKey:@"geo_lng"];
   
    return mutableDict;
}
@end
