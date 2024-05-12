package org.example.service;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
public class useARC {
    private static final int INIT_CAPACITY = 1; // 示例初始容量
    private static final int INIT_TRANSFORM_TIME = 3; // 示例初始转变次数

    private LRUcache Rcache;
    private LFUcache Fcache;
    private int size = 0;
    private int capacity;
    private int transformTime;

    // 默认构造函数
    public useARC() {
        this.capacity = INIT_CAPACITY;
        this.transformTime = INIT_TRANSFORM_TIME;
        this.Rcache = new LRUcache(INIT_CAPACITY);
        this.Fcache = new LFUcache(INIT_CAPACITY);
    }

    // 指定容量的构造函数
    public useARC(int capacity) {
        this.capacity = capacity;
        this.transformTime = INIT_TRANSFORM_TIME;
        this.Rcache = new LRUcache(capacity);
        this.Fcache = new LFUcache(capacity);
    }

    // 指定容量和转变次数的构造函数
    public useARC(int capacity, int transformTime) {
        this.capacity = capacity;
        this.transformTime = transformTime;
        this.Rcache = new LRUcache(capacity, transformTime);
        this.Fcache = new LFUcache(capacity, transformTime);
    }

    public void saveToARC(String data) {
        boolean find = false;
        // 先在各个表的ghost中搜索
        if (this.Rcache.checkGhost(data)) {
            // 缩减Fcache, 如果不成功,则调用put函数
            if (this.Fcache.subtract()) {
                this.Rcache.add(data);  // 扩容Rcache
            } else { // Fcache缩容失败,此时直接加入Rcache中
                this.Rcache.put(data);
            }
            find = true;
        }
        if (this.Fcache.checkGhost(data)) {
            if (this.Rcache.subtract()) {
                this.Fcache.add(data); // 扩容
            } else {
                this.Fcache.put(data); // 不扩容直接放
            }
            find = true;
        }

        // 如果两个的ghost中都没有对应的数, 则不改变partition(两个容量不改变)
        if (!find) {
            // 优先加入LRUcache中, 作为第一个元素
            if (this.Rcache.put(data)) this.Fcache.put(data);
        }
    }

    public List<String> get_ARC_list() {
        List<String> arcList = new ArrayList<>();
        // 从LRUcache添加数据
        arcList.addAll(Rcache.getKeys());
        arcList.addAll(Fcache.getKeys());
        return arcList;
    }

    public void clear() {
        // 清空 LRUcache
        this.Rcache.clear();

        // 清空 LFUcache
        this.Fcache.clear();

    }

    public void show(boolean showGhost) {
        System.out.println("%%%%%%%%%%%%%%%%%%% Begin %%%%%%%%%%%%%%%%%%%%%%");
        this.Rcache.show(showGhost);
        this.Fcache.show(showGhost);
        System.out.println("%%%%%%%%%%%%%%%%%%%% end %%%%%%%%%%%%%%%%%%%%%%%");
    }

    public static void main(String[] args) {
        useARC arcCache = new useARC(5, 3); // 假设初始化容量为10，转变时间为5
        arcCache.saveToARC("hello");
        arcCache.saveToARC("hella");
        arcCache.saveToARC("hellb");
        arcCache.saveToARC("world");
        arcCache.saveToARC("hellc");
        arcCache.saveToARC("hellk");
        arcCache.saveToARC("world");
        arcCache.saveToARC("world");
        arcCache.saveToARC("world");
        arcCache.saveToARC("hellb");
        arcCache.saveToARC("hellt");
        List<String> arcList = arcCache.get_ARC_list();
        for (String key : arcList) {
            System.out.println("Key: " + key);
        }
//        arcCache.clear();
        arcCache.show(true);  // 显示包括幽灵条目的全部缓存内容
        arcCache.saveToARC("hella");

        arcList = arcCache.get_ARC_list();
        for (String key : arcList) {
            System.out.println("Key: " + key);
        }
//        arcCache.clear();
        arcCache.show(true);  // 显示包括幽灵条目的全部缓存内容
    }
}


class CacheList {
    String key;
    int time = 0;
    CacheList prev = null;
    CacheList next = null;
}

class LRUcache {
    private static final int INIT_CAPACITY = 10;
    private static final int INIT_TRANSFORM_TIME = 3;

    private HashMap<String, CacheList> map = new HashMap<>();
    private CacheList cache = null;
    private int size = 0;
    private int capacity = INIT_CAPACITY;

    private HashMap<String, CacheList> ghostMap = new HashMap<>();
    private CacheList ghost = null;
    private int ghostSize = 0;
    private int ghostCapacity = INIT_CAPACITY;
    private int transformTime = INIT_TRANSFORM_TIME;

    public LRUcache() {
        this.capacity = INIT_CAPACITY;
        this.ghostCapacity = INIT_CAPACITY;
    }

    public LRUcache(int capacity) {
        this.capacity = capacity;
        this.ghostCapacity = capacity;
    }

    public LRUcache(int capacity, int transformTime) {
        this.capacity = capacity;
        this.ghostCapacity = capacity;
        this.transformTime = transformTime;
    }

    public void clear() {
        map.clear();
        ghostMap.clear();
        cache = null;
        ghost = null;
        size = 0;
        ghostSize = 0;
    }

    public boolean put(String data) {
        if (capacity == 0) return false;

        if (map.containsKey(data)) {
            CacheList node = map.get(data);
            node.time += 1;
            detachNode(node, false);
            insert(node, false);
            if (node.time >= transformTime) return true;
        } else {
            CacheList node = new CacheList();
            node.key = data;
            map.put(data, node);
            if (size == capacity) {
                deleteTail(false);
            }
            node.time = 1;
            insert(node, false);
        }
        return false;
    }

    private void insert(CacheList node, boolean isGhost) {
        if (node==null) return;
        if (isGhost) {
            if (ghost == null) {
                ghost = node;
                ghost.next = ghost;
                ghost.prev = ghost;
            }
            else {
                CacheList q = ghost.prev;
                node.next = ghost;
                node.prev = q;
                q.next = node;
                ghost.prev = node;
                this.ghost = node;
            }
            ghostSize++;
        }
        else {
            if (cache == null) {
                cache = node;
                cache.next = cache;
                cache.prev = cache;
            }
            else {
                CacheList q = cache.prev;
                node.next = cache;
                node.prev = q;
                cache.prev = node;
                q.next = node;
                this.cache = node;
            }
            size++;
        }
    }



    public void detachNode(CacheList L, boolean is_ghost) {
        // 注意需要判断是否为ghost，虽然代码相似，但是一个是size-1, 另一个ghost_size-1
        if (!is_ghost) { // 暂时移除指针，不抹除哈希表中的元素
            if (size == 0) {
                throw new RuntimeException("nothing to detach!");
            }
            if (size == 1) {
                cache = null;
            }
            else if (L == cache) { // 暂时清除头结点
                cache = L.next;
                cache.prev = L.prev;
                L.prev.next = cache;
            }
            else { // 取出中间节点
                CacheList q = L.prev;
                q.next = L.next;
                L.next.prev = q;
            }
            L.prev = null;
            L.next = null;
            size -= 1;
        }
        else {
            ghostMap.remove(L.key);
            // 注意: 由于之后指针会往主list中放, 不释放指针
            if (ghostSize == 1) {
                ghost = null;
            }
            else if (L == ghost) {
                // 删除头结点
                CacheList p = this.ghost;
                ghost = L.next; // 重置
                ghost.prev = L.prev;
                L.prev.next = ghost;
            }
            else { // 连接前后节点
                CacheList q = L.prev;
                q.next = L.next;
                L.next.prev = q;
            }
            // 重置L指针以便后续插入
            L.prev = null;
            L.next = null;
            L.time = 1;            // 将time重置为1
            ghostSize -= 1;
        }
    }

    public void deleteTail(boolean is_ghost) {
        if (!is_ghost) {
            CacheList remove;
            if (cache == null) throw new RuntimeException("no element to delete");
            else if (size == 1) {
                remove = cache;
                cache = null; // 更新头结点
            }
            else {
                // 移除 cache 的 prev 节点即尾结点
                remove = cache.prev;
                CacheList q = remove.prev;
                q.next = cache;
                cache.prev = q;
            }
            // 重整 remove 节点
            remove.prev = null;
            remove.next = null;
            remove.time = 0;
            // 清除哈希表中的元素
            map.remove(remove.key);

            if (ghostSize == ghostCapacity) deleteTail(true);
            // 对于 ghost 缓存满，则先移除元素，再插入到 ghost 数组中，不满则不移除元素
            // 然后将元素加入 ghost 中，显然 ghost 中是没有对应的项的
            ghostMap.put(remove.key, remove);
            insert(remove, true);
            size -= 1;
        }
        else { // 删除 ghost 数组中的尾部并移除
            if (ghost == null) throw new RuntimeException("no element to delete");
            CacheList remove;
            if (ghostSize == 1) {
                // 对于 ghost 中有一个元素并被取出时，删除头结点
                remove = this.ghost;
                this.ghost = null;
            }
            else {
                remove = ghost.prev;
                CacheList q = remove.prev;
                q.next = ghost;
                ghost.prev = q;
            }
            ghostMap.remove(remove.key); // 删除哈希表元素

            ghostSize -= 1;
        }
    }


    public void add(String data) {
        capacity++;
        CacheList node = new CacheList();
        node.key = data;
        node.time = 1;
        map.put(data, node);
        insert(node, false);
    }

    public boolean subtract() {
        if (capacity == 0) return false;
        if (size == capacity) deleteTail(false);
        capacity--;
        return true;
    }

    public boolean checkGhost(String data) {
        if (ghostMap.containsKey(data)) {
            detachNode(ghostMap.get(data), true);
            return true;
        }
        return false;
    }
    public void show(boolean showGhost) {
        CacheList current = this.cache;
        System.out.println("=============== Least Recently Used =========");
        if (current == null) {
            System.out.println("----- Empty list: nothing to show -----");
            return;
        }
        do {
            System.out.println("Key: " + current.key + ", Time: " + current.time);
            current = current.next;
        } while (current != cache);

        if (showGhost) {
            System.out.println("------------ Ghost Entries ----------------");
            current = ghost;
            if (current == null) {
                System.out.println("----- No ghost entries -----");
                return;
            }
            do {
                System.out.println("Key: " + current.key + ", Time: " + current.time);
                current = current.next;
            } while (current != ghost);
        }
        System.out.println("=============== End ====================");
    }

    public List<String> getKeys() {
        return new ArrayList<>(map.keySet());
    }

}


class LFUcache {
    private static final int INIT_CAPACITY = 10;
    private static final int INIT_TRANSFORM_TIME = 3;

    private Map<String, CacheList> map = new HashMap<>();
    private Map<String, CacheList> ghostMap = new HashMap<>();
    private CacheList cache = null;
    private CacheList ghost = null;
    private int size = 0;
    private int capacity = INIT_CAPACITY;
    private int ghostSize = 0;
    private int ghostCapacity = INIT_CAPACITY;
    private int transformTime = INIT_TRANSFORM_TIME;

    public LFUcache() {
        this.capacity = INIT_CAPACITY;
        this.ghostCapacity = INIT_CAPACITY;
    }

    public LFUcache(int capacity) {
        this.capacity = capacity;
        this.ghostCapacity = capacity;
    }

    public LFUcache(int capacity, int transformTime) {
        this.capacity = capacity;
        this.ghostCapacity = capacity;
        this.transformTime = transformTime;
    }

    public void clear() {
        map.clear();
        ghostMap.clear();
        cache = null;
        ghost = null;
        size = 0;
        ghostSize = 0;
    }
    public void put(String data) {
        if (this.capacity == 0) return;
        if (map.containsKey(data)) {
            CacheList L = map.get(data);
            detachNode(L, false);
            L.time += 1;
            insert(L, false);
//            System.out.println("after insert:" + L.time);
        } else {
            CacheList L = new CacheList();
            if (this.size == this.capacity) {
                deleteTail(false);
            }
            L.key = data;
            L.time = this.transformTime;
//            System.out.println("putting : " + L.key + " the time is " + this.transformTime);
            map.put(data, L);
            insert(L, false);
        }
    }

    public void insert(CacheList L, boolean is_ghost) {
        if (!is_ghost) {
            // 有序插入算法
            if (cache == null) { // 空则建立结点
                this.cache = L;
                cache.next = cache;
                cache.prev = cache;
            }
            else if (cache.time < L.time) { // 插入到头结点上
                CacheList p = cache.prev;
                L.next = cache;
                L.prev = p;
                p.next = L;
                cache.prev = L;
                this.cache = L; // 更新头结点
            }
            else {
                CacheList pre = cache;
                // 在循环链表中有序插入元素 -> 此处有不足，如果使用双哈希表可以得到O(1)的复杂度
                for (; pre.next != cache && pre.next.time > L.time; pre = pre.next);
                // 最后停在最末端元素上,将L插入到pre的尾部即可
                CacheList q = pre.next;
                L.prev = pre;
                L.next = q;
                pre.next = L;
                q.prev = L;
            }
            this.size += 1;
        }
        else{ // 将节点插入 ghost
            if (ghost == null) {
                this.ghost = L;
                ghost.next = ghost;
                ghost.prev = ghost;
            }
            else if (ghost.time < L.time) {
                CacheList p = ghost.prev;
                L.next = ghost;
                L.prev = p;
                p.next = L;
                ghost.prev = L;
                this.ghost = L;  // 更新头结点
            }
            else {
                CacheList pre = ghost;
                // 在循环链表中有序插入元素
                for (; pre.next != ghost && pre.next.time > L.time; pre = pre.next);
                // 将L插入到pre的尾部即可
                CacheList q = pre.next;
                L.prev = pre;
                L.next = q;
                pre.next = L;
                q.prev = L;
            }
            this.ghostSize += 1;
        }
    }


    public void detachNode(CacheList L, boolean is_ghost) {
        // Detach操作相同,直接复制粘贴FRUfunc的代码
        if (!is_ghost) {
            if (this.size == 0) {
                throw new RuntimeException("nothing to detach!");
            }
            if (this.size == 1) {
                this.cache = null;
            }
            else if (L == cache) { // 暂时清除头结点
                this.cache = L.next;
                cache.prev = L.prev;
                L.prev.next = cache;
            }
            else { // 取出中间节点
                CacheList q = L.prev;
                q.next = L.next;
                L.next.prev = q;
            }
            L.prev = null;
            L.next = null;
            size -= 1;
        }
        else {
            ghostMap.remove(L.key);
            // 注意: 由于之后指针会往主list中放, 不释放指针
            if (this.ghostSize == 1) {
                this.ghost = null;
            }
            else if (L == ghost) {
                // 删除头结点
                CacheList p = this.ghost;
                this.ghost = L.next; // 重置
                ghost.prev = L.prev;
                L.prev.next = ghost;
            }
            else { // 连接前后节点
                CacheList q = L.prev;
                q.next = L.next;
                L.next.prev = q;
            }
            // 重置L指针以便后续插入
            L.prev = null;
            L.next = null;
            L.time = 1;            // 将time重置为1
            ghostSize -= 1;
        }
    }


    public void add(String data) {
        this.capacity++;
        CacheList L = new CacheList();
        L.key = data;
        L.time = 1;
        map.put(data, L);
        insert(L, false);
    }

    public boolean subtract() {
        if (this.capacity == 0) return false;
        if (this.size == this.capacity) {
            deleteTail(false);
        }
        this.capacity--;
        return true;
    }


    public void deleteTail(boolean is_ghost) {
        if (!is_ghost) {
            CacheList remove;
            if (cache == null)
                throw new RuntimeException("no element to delete");
            else if (size == 1) { // 删除头结点
                remove = cache;
                cache = null;
            }
            else { // 移除尾结点
                remove = cache.prev;
                CacheList q = remove.prev;
                q.next = cache;
                cache.prev = q;
            }
            remove.prev = null;
            remove.next = null;
            remove.time = 0;
            map.remove(remove.key);
            if (ghostSize == ghostCapacity) deleteTail(true);

            ghostMap.put(remove.key, remove); // 加入到 ghostMap 的哈希表中
            insert(remove, true);
            size -= 1;
        }
        else { // 删除 ghost 中的结尾元素
            if (ghost == null)
                throw new RuntimeException("no element to delete");
            CacheList remove;
            if (ghostSize == 1) {
                remove = ghost;
                ghost = null;
            }
            else {
                remove = ghost.prev;
                CacheList q = remove.prev;
                q.next = ghost;
                ghost.prev = q;
            }
            ghostMap.remove(remove.key);

            ghostSize -= 1;
        }
    }



    public boolean checkGhost(String data) {
        if (ghostMap.containsKey(data)) {
            CacheList L = ghostMap.get(data);
            detachNode(L, true);
            return true;
        }
        return false;
    }

    public void show(boolean showGhost) {
        CacheList current = this.cache;
        System.out.println("=============== Least Frequently Used =========");
        if (current == null) {
            System.out.println("----- Empty list: nothing to show -----");
            return;
        }
        do {
            System.out.println("Key: " + current.key + ", Time: " + current.time);
            current = current.next;
        } while (current != cache);

        if (showGhost) {
            System.out.println("------------ Ghost Entries ----------------");
            current = ghost;
            if (current == null) {
                System.out.println("----- No ghost entries -----");
                return;
            }
            do {
                System.out.println("Key: " + current.key + ", Time: " + current.time);
                current = current.next;
            } while (current != ghost);
        }
        System.out.println("=============== End ====================");
    }

    public List<String> getKeys() {
        return new ArrayList<>(map.keySet());
    }

}
