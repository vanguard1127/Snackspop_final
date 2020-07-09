//
//  TermsViewController.h
//  snackspopnew
//
//  Created by Admin on 2019-04-09.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <WebKit/WebKit.h>
#import "../Utils/AppUtils.h"
NS_ASSUME_NONNULL_BEGIN

@interface TermsViewController : UIViewController
- (IBAction)onBtnBackClick:(id)sender;
@property (weak, nonatomic) IBOutlet UIWebView *mWebView;


@end

NS_ASSUME_NONNULL_END
