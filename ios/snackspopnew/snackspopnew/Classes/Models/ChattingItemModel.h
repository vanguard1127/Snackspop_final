//
//  ChattingItemModel.h
//  snackspopnew
//
//  Created by Admin on 2019-04-10.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface ChattingItemModel : NSObject
@property (readwrite, nonatomic, assign) NSInteger chat_id;
@property (readwrite , nonatomic ,assign) NSInteger userid;
@property (readwrite, nonatomic, copy) NSDate *date;
@property (readwrite, nonatomic, copy) NSString *message;
@property (readwrite, nonatomic, assign) NSInteger direction;
@property (readwrite, nonatomic, copy) NSString * image1Url;
@property (readwrite, nonatomic, copy) NSString * image2Url;

@end

NS_ASSUME_NONNULL_END
