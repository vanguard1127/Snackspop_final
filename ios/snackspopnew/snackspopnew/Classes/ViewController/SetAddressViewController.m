//
//  SetAddressViewController.m
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "SetAddressViewController.h"

@interface SetAddressViewController ()

@end

@implementation SetAddressViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    NSDictionary *userObject = [AppUtils getUserInfo];
    NSString *address_line1  = [AppUtils getStringFromDictionary:userObject objectForKey:@"address_line1"];
    NSString *address_line2 = [AppUtils getStringFromDictionary:userObject objectForKey:@"address_line2"];
    NSString *city = [AppUtils getStringFromDictionary:userObject objectForKey:@"city"];
    NSString *state = [AppUtils getStringFromDictionary:userObject objectForKey:@"county_state_province"];

    NSString *country = [AppUtils getStringFromDictionary:userObject objectForKey:@"country"];;
    
    id mZipCode = [userObject objectForKey:@"zip_code"] ;
    NSString *zipcode = @"";
    if ((NSNull *)mZipCode != [NSNull null])
    {
    
        NSInteger zip = [mZipCode integerValue];
        zipcode = [NSString stringWithFormat:@"%ld",zip];
    }
    
    
    self.mAddrLine1.text = address_line1;
    self.mAddrLine2.text = address_line2;
    self.mCity.text  = city;
    self.mState.text = state;
    self.mZipCode.text = zipcode;
    self.mCountry.text = country;
    
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/
-(NSString *)getType:(NSString *)types
{

    if ([types containsString:@"\"localcity\""])
    {
        return @"city";
    }
    else if ([types containsString:@"\"administrative_area_level_1\""])
    {
        return @"state";
    }
    else if ([types containsString:@"\"country\""])
    {
        return @"country";
    }
    else if ([types containsString:@"\"postal_code\""])
    {
        return @"zipcode";
        
    }
    else if ([types containsString:@"\"sublocality\""])
    {
        return @"address2";
    }
    return @"none";
}
-(void)startAsyncGetMyLocation
{
    NSString *lat= @"";
    NSString *lng = @"";
    
    NSString *url =@"http://maps.google.com/maps/api/geocode/json?latlng=";
    url = [url stringByAppendingString:lat];
    url = [url stringByAppendingString:@","];
    url = [url stringByAppendingString:lng];
    url = [url stringByAppendingString:@"&sensor=false"];

    [AppUtils addActivityIndicator:self.view];
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [manager.requestSerializer setValue:[AppUtils getUserToken] forHTTPHeaderField:@"x-access-token"];
    
    [manager GET:url
      parameters:nil
        progress:^(NSProgress * _Nonnull downloadProgress) {
            
            
        } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
            NSLog(@"Google Map Get Sucess");
            NSLog(@"JSON: %@", responseObject);
            
            NSArray *array = [responseObject objectForKey:@"result"];
            NSDictionary * dictArray = [array objectAtIndex:0];
            NSArray *addressArray = [dictArray objectForKey:@"address_components"];
            for (NSDictionary *dict in addressArray)
            {
                NSString *typestring = [dict objectForKey:@"types"];
                
                if ([[self getType:typestring] isEqualToString:@"city" ])
                {
                    NSString *str = [dict objectForKey:@"long_name"];
                    self.mCity.text = str;
                }
                else if ([[self getType:typestring] isEqualToString:@"state" ])
                {
                    NSString *str = [dict objectForKey:@"long_name"];
                    self.mState.text = str;
                }
                else if ([[self getType:typestring] isEqualToString:@"country"] )
                {
                    NSString *str = [dict objectForKey:@"long_name"];
                    self.mCountry.text = str;
                }
                else if ([[self getType:typestring] isEqualToString:@"zipcode"] )
                {
                    NSString *str = [dict objectForKey:@"long_name"];
                    self.mZipCode.text = str;
                    
                }
                else if ([[self getType:typestring] isEqualToString:@"address2"] )
                {
                    NSString *str = [dict objectForKey:@"long_name"];
                    self.mAddrLine2.text = str;
                }
                else
                {
                    NSString *mAddressString1 = self.mAddrLine1.text;
                    NSString *addr = [dict objectForKey:@"long_name"];
                    self.mAddrLine1.text = [[mAddressString1 stringByAppendingString:@","] stringByAppendingString:addr];
                }
            }
            [AppUtils removeActivityIndicator:self.view];
        } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
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
-(void)startUpdateAddress
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
    
    NSString *addressLine1 = self.mAddrLine1.text;
    NSString *addressLine2 = self.mAddrLine2.text;
    NSString *country = self.mCountry.text;
    NSString *state = self.mState.text;
    NSString *city = self.mCity.text;
    NSString *zipcod = self.mZipCode.text;

    
    
    [mutableDict setObject:addressLine1 forKey:@"address_line1"];
    
    [mutableDict setObject:addressLine2 forKey:@"address_line2"];
     [mutableDict setObject:country forKey:@"country"];
     [mutableDict setObject:state forKey:@"county_state_province"];
     [mutableDict setObject:city forKey:@"city"];
     [mutableDict setObject:zipcod forKey:@"zip_code"];
    
    
    return mutableDict;
}

- (IBAction)onBtnSave:(id)sender {
    [self startUpdateAddress];
}

- (IBAction)onBtnBack:(id)sender {
    [self dismissViewControllerAnimated:YES completion:NULL];}
@end
