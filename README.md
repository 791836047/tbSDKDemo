需求说明：
算法大佬从2000万用户中筛选出400W符合要求的用户信息后，清洗出淘宝会员id(specialId)到txt资源文件中，
我的任务就是拿这400W个specialId去调淘宝客的活动接口来判断用户是否符合活动目标，
将返回的结果活动状态status和specialId关联后存入txt文件中,返回结果中参数状态1表时用户符合活动要求，3表示用户不匹配活动。
