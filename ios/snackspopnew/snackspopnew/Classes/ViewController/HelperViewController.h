//
//  HelperViewController.h
//  snackspopnew
//
//  Created by Admin on 2019-04-05.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "../SubView/HelperSubViewController.h"
#import "SignInViewController.h"
#import "HomeViewController.h"

NS_ASSUME_NONNULL_BEGIN

@interface HelperViewController : UIViewController<UIPageViewControllerDataSource>
@property (strong, nonatomic) UIPageViewController *pageController;
@property (strong, nonatomic) IBOutlet UIButton* btnSkip;
- (IBAction)btnSkipClick:(id)sender;

@end

NS_ASSUME_NONNULL_END
