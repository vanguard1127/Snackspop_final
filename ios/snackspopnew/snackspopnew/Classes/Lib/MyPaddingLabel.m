//
//  MyPaddingLabel.m
//  snackspopnew
//
//  Created by Admin on 2019-04-16.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "MyPaddingLabel.h"

@implementation MyPaddingLabel
- (void)drawTextInRect:(CGRect)rect {
    UIEdgeInsets insets = {5, 5, 5, 5};
    [super drawTextInRect:UIEdgeInsetsInsetRect(rect, insets)];
}

- (CGSize) intrinsicContentSize {
    CGSize intrinsicSuperViewContentSize = [super intrinsicContentSize] ;
    intrinsicSuperViewContentSize.height +=10;
    intrinsicSuperViewContentSize.width += 10 ;
    return intrinsicSuperViewContentSize ;
}
@end
