//
//  AppDelegate.m
//  snackspopnew
//
//  Created by Admin on 2019-04-04.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "AppDelegate.h"
#import "KafkaRefresh.h"
#import <FBSDKCoreKit/FBSDKCoreKit.h>
#import <UserNotifications/UserNotifications.h>
#import "Classes/Utils/AppUtils.h"
@import Firebase;
@import GoogleSignIn;
@import IQKeyboardManager;

@interface AppDelegate ()

@end

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
    
    
    [[KafkaRefreshDefaults standardRefreshDefaults] setHeadDefaultStyle:KafkaRefreshStyleAnimatableRing];
    
    [[FBSDKApplicationDelegate sharedInstance] application:application
                             didFinishLaunchingWithOptions:launchOptions];
    
    [GIDSignIn sharedInstance].clientID = @"466050876426-38jr1dd4s36e6hngf53bn17r595upl6t.apps.googleusercontent.com";

    
    [FIRApp configure];
    
    [self registerForRemoteNotifications];
    
    [[IQKeyboardManager sharedManager] setEnable:true];

    [[IQKeyboardManager sharedManager].disabledDistanceHandlingClasses addObject:NSClassFromString(@"ChattingViewController")];
    [AppUtils setAppRunning:0];
    
    [AppUtils setEnteredChatting:NO];
    return YES;
}

- (void)registerForRemoteNotifications {
    if(SYSTEM_VERSION_GRATERTHAN_OR_EQUALTO(@"10.0")){
        UNUserNotificationCenter *center = [UNUserNotificationCenter currentNotificationCenter];
        center.delegate = self;
        [center requestAuthorizationWithOptions:(UNAuthorizationOptionSound | UNAuthorizationOptionAlert | UNAuthorizationOptionBadge) completionHandler:^(BOOL granted, NSError * _Nullable error){
            if(!error){
                [[UIApplication sharedApplication] registerForRemoteNotifications];
            }
        }];
    }
    else {
        // Code for old versions
    }
}

//Called when a notification is delivered to a foreground app.
-(void)userNotificationCenter:(UNUserNotificationCenter *)center willPresentNotification:(UNNotification *)notification withCompletionHandler:(void (^)(UNNotificationPresentationOptions options))completionHandler{
    completionHandler(UNAuthorizationOptionSound | UNAuthorizationOptionAlert | UNAuthorizationOptionBadge);
    
    
}

//Called to let your app know which action was selected by the user for a given notification.
-(void)userNotificationCenter:(UNUserNotificationCenter *)center didReceiveNotificationResponse:(UNNotificationResponse *)response withCompletionHandler:(void(^)())completionHandler{
    
    
    NSLog(@"User Info : %@",response.notification.request.content.userInfo);
    //TODO
    NSDictionary *userInfo = response.notification.request.content.userInfo;
    
    chatting_flag = 1;
    NSString *string = [userInfo objectForKey:@"user_info"];
    NSError *jsonError;
    NSData *data = [string dataUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *     send_user_info
    = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers  error:&jsonError];
    NSInteger userid = [[send_user_info objectForKey:@"id"] integerValue];
    NSString *firstName = [send_user_info objectForKey:@"first_name"];
    NSString *lastName = [send_user_info objectForKey:@"last_name"];
    
    id photoID = [send_user_info objectForKey:@"photo"];
;
    NSInteger photoId = -1;
    if ((NSNull *)photoID != [NSNull null])
    {
        photoId = [[send_user_info objectForKey:@"photo"] integerValue];
    }
    NSString *photoString = [NSString stringWithFormat:@"%ld",photoId ];
    
    NSInteger itemid = [[userInfo objectForKey:@"item_id"] integerValue];
    NSString *userimageurl = [[BASE_URL stringByAppendingString:IMAGE_URL] stringByAppendingString:photoString];
    
    [AppUtils setNotifiUserData:userid
                         itemID:itemid
                          fName:firstName
                          lName:lastName
                         uImage:userimageurl];
    
    
    
    NSInteger isRunning = [AppUtils isAppRunning];
    
    if (isRunning == 1)
        [self showChattingController];
    else
        [AppUtils setChatPush:1];
    completionHandler();
    
    
}

-(void)showChattingController
{
    if (chatting_flag)
    {
        ChatItemModel *m_model = [AppUtils getNotifiUserData];
        UIViewController *currentTopVC = [AppUtils currentTopViewController];
        
        if ([currentTopVC isKindOfClass:[ChattingViewController class]]) {
            ChattingViewController *viewController = (ChattingViewController *)currentTopVC;
            [viewController refreshViewController:m_model];
        } else {
            ChattingViewController *view = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"ChattingViewId"];
            view.model = m_model;
            [currentTopVC presentViewController:view animated:true completion:nil];
        }
        
        
        
    }
}
- (void)applicationWillResignActive:(UIApplication *)application {
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
//    [AppUtils setAppRunning:NO];

    
    
}


- (void)applicationDidEnterBackground:(UIApplication *)application {
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    if ((NSNull *)AppUtils.socketManager != [NSNull null] && [AppUtils getEnteredChatting] == YES) {
        [AppUtils.mSocket disconnect];
    }
}


- (void)applicationWillEnterForeground:(UIApplication *)application {
    // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
    if (AppUtils.mEnteredChatting == 1){
        if ((NSNull *)AppUtils.socketManager != [NSNull null] && [AppUtils.socketManager status] == SocketIOStatusConnected) return;
        NSURL* url = [[NSURL alloc] initWithString:BASE_SITE_URL];
        SocketManager* manager = [[SocketManager alloc] initWithSocketURL:url config:@{@"log": @YES, @"compress": @YES}];
        AppUtils.socketManager = manager;
        
        SocketIOClient* socket = manager.defaultSocket;
        AppUtils.mSocket = socket;
    }
    
}


- (void)applicationDidBecomeActive:(UIApplication *)application {
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    
}


- (void)applicationWillTerminate:(UIApplication *)application {
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    
    [AppUtils setAppRunning:0];
}



- (BOOL)application:(UIApplication *)app
            openURL:(NSURL *)url
            options:(NSDictionary<NSString *, id> *)options {
    BOOL handled = [[FBSDKApplicationDelegate sharedInstance] application:app
                                                                  openURL:url
                                                        sourceApplication:options[UIApplicationOpenURLOptionsSourceApplicationKey]
                                                               annotation:options[UIApplicationOpenURLOptionsAnnotationKey]
                    ];
    // Add any custom logic here.
    
    
    BOOL ghandled =  [[GIDSignIn sharedInstance] handleURL:url
                               sourceApplication:options[UIApplicationOpenURLOptionsSourceApplicationKey]
                                      annotation:options[UIApplicationOpenURLOptionsAnnotationKey]];
    
    return handled && ghandled;
}
- (void)signIn:(GIDSignIn *)signIn
didSignInForUser:(GIDGoogleUser *)user
     withError:(NSError *)error {

    // ...
}

- (void)signIn:(GIDSignIn *)signIn
didDisconnectWithUser:(GIDGoogleUser *)user
     withError:(NSError *)error {
    // Perform any operations when the user disconnects from app here.
    // ...
}

@end

