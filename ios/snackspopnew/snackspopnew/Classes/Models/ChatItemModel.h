//
//  ChatItemModel.h
//  snackspopnew
//
//  Created by Admin on 2019-04-09.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface ChatItemModel : NSObject
@property (readwrite, nonatomic, assign) NSUInteger itemId;
@property (readwrite, nonatomic, copy) NSString *itemName;
@property (readwrite, nonatomic, copy) NSString *lastMessage;
@property (readwrite, nonatomic, assign) NSInteger unreadCount;
@property (readwrite, nonatomic, assign) NSInteger room_from_user_id;
@property (readwrite, nonatomic, assign) NSInteger room_to_user_id;
@property (readwrite, nonatomic, assign) NSInteger item_user_id;
@property( readwrite ,nonatomic, copy) NSString *item_image_id;
@property (readwrite, nonatomic, assign) NSInteger user_id;

@property( readwrite ,nonatomic, copy) NSString *user_first_name;
@property( readwrite ,nonatomic, copy) NSString *user_last_name;
@property( readwrite ,nonatomic, copy) NSString *user_photo_id;
@end

NS_ASSUME_NONNULL_END
