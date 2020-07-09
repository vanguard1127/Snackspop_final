//
//  HelperSubViewController.m
//  snackspopnew
//
//  Created by Admin on 2019-04-05.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "HelperSubViewController.h"

@interface HelperSubViewController ()

@end

@implementation HelperSubViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    if (_pageIndex == 0)
    {
        HelperView1 *newView = [[[NSBundle mainBundle] loadNibNamed:@"HelperView1" owner:self options:nil] objectAtIndex:0];
        newView.frame = CGRectMake(0, 0,  self.view.frame.size.width, self.view.frame.size.height);
        [self.view addSubview:newView];

    }
    else if (_pageIndex == 1)
    {
        HelperView2 *newView = [[[NSBundle mainBundle] loadNibNamed:@"HelperView2" owner:self options:nil] objectAtIndex:0];
        newView.frame = CGRectMake(0, 0,  self.view.frame.size.width, self.view.frame.size.height);
        [self.view addSubview:newView];
    }
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
