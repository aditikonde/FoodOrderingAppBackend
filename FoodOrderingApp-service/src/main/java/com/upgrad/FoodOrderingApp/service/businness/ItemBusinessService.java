package com.upgrad.FoodOrderingApp.service.businness;


import com.upgrad.FoodOrderingApp.service.dao.OrderItemDao;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ItemBusinessService {

//    @Autowired
//    private ItemDao itemDao;
//
//    @Autowired
//    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;

//    @Transactional
//    // A Method which takes the itemId as parameter for getItemEntityById
//    public ItemEntity getItemEntityById(final Integer itemId){
//
//        return itemDao.getItemById(itemId);
//    }
//
//    @Transactional
//    // A Method which takes the item uuid as parameter for getItemEntityByUuid
//    public ItemEntity getItemEntityByUuid(final String itemUuid) throws ItemNotFoundException{
//
//        ItemEntity itemEntity = itemDao.getItemByUuid(itemUuid);
//        if (itemEntity == null) {
//            throw new ItemNotFoundException("INF-003", "No item by this id exist");
//        } else {
//            return itemEntity;
//        }
//    }
//
//    @Transactional
//    public List<ItemEntity> getItemsByPopularity(RestaurantEntity restaurantEntity) {
//
//        // List to store all items ordered in a restaurant
//        List<ItemEntity> itemEntityList = new ArrayList<>();
//
//        // Gets all the orders placed in the restaurant
//        for (OrdersEntity orderEntity : orderDao.getOrdersByRestaurant(restaurantEntity)) {
//            // Gets items from each order placed in the restaurant
//            for (OrderItemEntity orderItemEntity : orderItemDao.getItemsByOrder(orderEntity)) {
//                itemEntityList.add(orderItemEntity.getItem());
//            }
//        }
//
//        // Load all the item entities to hashmap
//        Map<String, Integer> map = new HashMap<String, Integer>();
//        for (ItemEntity itemEntity : itemEntityList) {
//            Integer count = map.get(itemEntity.getUuid());
//            map.put(itemEntity.getUuid(), (count == null) ? 1 : count + 1);
//        }
//
//        // Sorts item entities
//        Map<String, Integer> treeMap = new TreeMap<String, Integer>(map);
//        List<ItemEntity> sortedItemEntityList = new ArrayList<ItemEntity>();
//        for (Map.Entry<String, Integer> entry : treeMap.entrySet()) {
//            sortedItemEntityList.add(itemDao.getItemByUuid(entry.getKey()));
//        }
//
//        // Reverse sort the collections
//        Collections.reverse(sortedItemEntityList);
//
//        return sortedItemEntityList;
//    }

    public List<OrderItemEntity> getItemsByOrder(OrdersEntity orderEntity) {
        return orderItemDao.getItemsByOrder(orderEntity);
    }
}

