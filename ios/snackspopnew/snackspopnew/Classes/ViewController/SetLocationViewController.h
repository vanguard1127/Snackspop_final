//
//  SetLocationViewController.h
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SetAddressViewController.h"
#import "../Utils/AppUtils.h"
#import "SetAddressViewController.h"
#import <MapKit/MapKit.h>

NS_ASSUME_NONNULL_BEGIN
typedef void (^disMissCallback)(NSInteger index);


@interface SetLocationViewController :UIViewController <MKMapViewDelegate>
- (IBAction)onBtnBack:(id)sender;
- (IBAction)onBtnSave:(id)sender;
- (IBAction)onBtnSetAddress:(id)sender;
- (IBAction)onBtnMyLocation:(id)sender;
@property (weak, nonatomic) IBOutlet MKMapView *mMapView;

@property (weak, nonatomic) IBOutlet UILabel *mAddressView;

@property (weak, nonatomic) IBOutlet UIView *mAddresContainerView;

@property (strong , nonatomic) CLLocation *myCurrentPosition;

@property (assign , nonatomic) NSInteger mAddressAppeared;
@property (copy, nonatomic) disMissCallback mdismisCallBack;

-(void)startUpdateLocation;
-(NSDictionary *)makeRequestParams;

@end

NS_ASSUME_NONNULL_END
