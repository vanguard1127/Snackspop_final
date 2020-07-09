//
//  TermsViewController.m
//  snackspopnew
//
//  Created by Admin on 2019-04-09.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "TermsViewController.h"

@interface TermsViewController ()

@end

@implementation TermsViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
//    NSString *urlString = [BASE_SITE_URL stringByAppendingString:@"/aboutus"];
    NSString *urlString = @"http://www.snackspop.com/seller.html";
    NSURL *url = [NSURL URLWithString:urlString];
    NSURLRequest *urlRequest = [NSURLRequest requestWithURL:url];
    [_mWebView loadRequest:urlRequest];
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

- (IBAction)onBtnBackClick:(id)sender {
    [self dismissViewControllerAnimated:YES completion:NULL];
    
}
@end
