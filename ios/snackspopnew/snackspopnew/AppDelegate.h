//
//  AppDelegate.h
//  snackspopnew
//
//  Created by Admin on 2019-04-04.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <UIKit/UIKit.h>

#import <UserNotifications/UserNotifications.h>
#import "Classes/ViewController/ChattingViewController.h"
#import "Classes/Models/ChatItemModel.h"
@import GoogleSignIn;

@interface AppDelegate : UIResponder <UIApplicationDelegate , UNUserNotificationCenterDelegate>

@property (strong, nonatomic) UIWindow *window;


@end

