//
//  HelperViewController.m
//  snackspopnew
//
//  Created by Admin on 2019-04-05.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "HelperViewController.h"

@interface HelperViewController ()

@end

@implementation HelperViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.pageController = [[UIPageViewController alloc] initWithTransitionStyle:UIPageViewControllerTransitionStyleScroll navigationOrientation:UIPageViewControllerNavigationOrientationHorizontal options:nil];
    
    self.pageController.dataSource = self;
    [[self.pageController view] setFrame:[[self view] bounds]];
    
    
    HelperSubViewController *startingViewController = [self viewControllerAtIndex:0];
    NSArray *viewControllers = @[startingViewController];
    [self.pageController setViewControllers:viewControllers direction:UIPageViewControllerNavigationDirectionForward animated:NO completion:nil];
    
    self.pageController.view.frame = CGRectMake(0, 0,  [UIScreen mainScreen].bounds.size.width, self.view.frame.size.height);
    self.pageController.view.backgroundColor = [UIColor colorWithWhite:0.5
                                                                 alpha:0.0];
    [self addChildViewController: _pageController];
    [self.view addSubview:_pageController.view];
    [self.pageController didMoveToParentViewController:self];
    
    [self.view bringSubviewToFront:_btnSkip];
    
}
- (UIViewController *)pageViewController:(UIPageViewController *)pageViewController viewControllerBeforeViewController:(UIViewController *)viewController
{
    NSUInteger index = ((HelperSubViewController*) viewController).pageIndex;
    
    if ((index == 0) || (index == NSNotFound)) {
        return nil;
    }
    index--;
    return [self viewControllerAtIndex:index];
}



- (UIViewController *)pageViewController:(UIPageViewController *)pageViewController viewControllerAfterViewController:(UIViewController *)viewController
{
    NSUInteger index = ((HelperSubViewController*) viewController).pageIndex;
    
    if (index == NSNotFound) {
        return nil;
    }
    
    index++;
    if (index == 2) {
        return nil;
    }
    return [self viewControllerAtIndex:index];
}


- (HelperSubViewController *)viewControllerAtIndex:(NSUInteger)index
{
    if ((index < 0) || (index >= 2)) {
        return nil;
    }
    
    // Create a new view controller and pass suitable data.
    HelperSubViewController *helperSubViewController = [HelperSubViewController alloc];
    helperSubViewController.pageIndex = index;
    
    return helperSubViewController;
}

- (NSInteger)presentationCountForPageViewController:(UIPageViewController *)pageViewController
{
    return 2;
}

- (NSInteger)presentationIndexForPageViewController:(UIPageViewController *)pageViewController
{
    return 0;
}

- (IBAction)btnSkipClick:(id)sender {
    [AppUtils setNotFirstTime];
    UIViewController *currentTopVC = [AppUtils currentTopViewController];
    HomeViewController *controller = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"HomeViewId"];
    [currentTopVC presentViewController:controller animated:true completion:nil];
    
}
@end
