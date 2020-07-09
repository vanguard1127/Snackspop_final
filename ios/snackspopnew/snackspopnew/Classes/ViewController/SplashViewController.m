//
//  SplashViewController.m
//  snackspopnew
//
//  Created by Admin on 2019-04-05.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "SplashViewController.h"
#import "HelperViewController.h"
@interface SplashViewController ()

@end

@implementation SplashViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    /*[NSTimer
     scheduledTimerWithTimeInterval:0.001
     target:self
     selector:@selector(onTick:)
     userInfo:nil
     repeats:YES
     ];*/
    
    [NSTimer scheduledTimerWithTimeInterval:3.0
                                     target:self
                                   selector:@selector(targetMethod:)
                                   userInfo:nil
                                    repeats:NO];

}

- (void)onTick:(NSTimer *)timer {
    NSLog(@"received timer event: %@", [[timer fireDate] description]);
    //[timer invalidate];
    //timer = nil;

}

- (void) targetMethod:(id)sender{
    NSLog(@"time out");
    [self dismissViewControllerAnimated:YES completion:NULL];
    if (![AppUtils isNotFirstTime]){
        
        HelperViewController *helper = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"HelperViewId"];
        [self presentViewController:helper animated:YES completion:nil];
    }
    else{
        HomeViewController *homeView = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"HomeViewId"];
        [self presentViewController:homeView animated:YES completion:nil];
    }

    
}
- (void) thisMethodGetsFiredOnceEveryThirtySeconds:(id)sender {
    NSLog(@"fired!");
}
/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
