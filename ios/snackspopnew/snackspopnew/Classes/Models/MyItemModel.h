//
//  MyItemModel.h
//  snackspopnew
//
//  Created by Admin on 2019-04-08.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface MyItemModel : NSObject
@property (readwrite, nonatomic, assign) NSUInteger itemId;
@property (readwrite, nonatomic, copy) NSString *itemName;
@property (readwrite, nonatomic, copy) NSString *itemDescription;
@property (readwrite, nonatomic, assign) NSInteger itemPrice;
@property (readwrite, nonatomic, copy) NSString *itemPriceUnit;
@property (readwrite ,nonatomic, assign) NSInteger userId;
@property( readwrite ,nonatomic, assign) double distance;
@property( readwrite ,nonatomic, copy) NSString *item_image_id;

@property (readwrite, nonatomic, copy) NSString *user_image_url;

@property (readwrite, nonatomic, unsafe_unretained) NSURL *avatarImageURL;

- (instancetype)initWithAttributes:(NSDictionary *)attributes;

@end
@interface MyItemModel (NSCoding) <NSSecureCoding>
@end
NS_ASSUME_NONNULL_END
