package io.geewit.core.utils.tree;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 标记过的树节点集合
 * @author geewit
 */
@Builder
@Setter
@Getter
public class SignKeysMap<Key extends Serializable> implements Map<Integer, Set<Key>> {

    /**
     * Constructor uses an existing <code>Map</code> to store the values.
     * @param map The <code>Map</code> backing this <code>SimpleBindings</code>.
     * @throws NullPointerException if m is null
     */
    public SignKeysMap(Map<Integer, Set<Key>> map) {
        if (map == null) {
            throw new NullPointerException();
        }
        this.map = map;
    }

    /**
     * Default constructor uses a <code>HashMap</code>.
     */
    public SignKeysMap() {
        this(new HashMap<>());
    }

    public <S extends NodeSign<Key>> SignKeysMap(Collection<S> nodeSigns) {
        this.map = new HashMap<>();
        if (nodeSigns == null || nodeSigns.isEmpty()) {
            return;
        }
        for (S nodeSign : nodeSigns) {
            if (nodeSign.getSign() == null) {
                continue;
            }
            Set<Key> keys = this.map.get(nodeSign.getSign());
            if (keys == null) {
                keys = Stream.of(nodeSign.getId()).collect(Collectors.toSet());
            } else {
                keys.add(nodeSign.getId());
            }
            this.map.put(nodeSign.getSign(), keys);
        }
    }

    private Map<Integer, Set<Key>> map;

    @Override
    public int size() {
        return map.size();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /** {@inheritDoc} */
    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    /** {@inheritDoc} */
    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    /** {@inheritDoc} */
    @Override
    public Set<Key> get(Object key) {
        return map.get(key);
    }

    /** {@inheritDoc} */
    @Override
    public Set<Key> put(Integer key, Set<Key> value) {
        return map.put(key, value);
    }

    @Override
    public Set<Key> remove(Object key) {
        return map.remove(key);
    }

    /** {@inheritDoc} */
    @Override
    public void putAll(Map<? extends Integer, ? extends Set<Key>> toMerge) {
        if (toMerge == null) {
            throw new NullPointerException("toMerge map is null");
        }
        for (Map.Entry<? extends Integer, ? extends Set<Key>> entry : toMerge.entrySet()) {
            Integer key = entry.getKey();
            this.put(key, entry.getValue());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void clear() {
        map.clear();
    }

    /** {@inheritDoc} */
    @Override
    public Set<Integer> keySet() {
        return map.keySet();
    }

    /** {@inheritDoc} */
    @Override
    public Collection<Set<Key>> values() {
        return map.values();
    }

    /** {@inheritDoc} */
    @Override
    public Set<Entry<Integer, Set<Key>>> entrySet() {
        return map.entrySet();
    }

    public void add(Integer sign, Key key) {
        if (key == null) {
            return;
        }
        Set<Key> keys = this.map.get(sign);
        if (keys == null) {
            keys = Stream.of(key).collect(Collectors.toSet());
        } else {
            keys.add(key);
        }
        this.map.put(sign, keys);
    }

    public void addAll(Integer sign, Collection<Key> values) {
        if (values == null || values.isEmpty()) {
            return;
        }
        Set<Key> keys = this.map.get(sign);
        if (keys == null) {
            keys = new HashSet<>(values);
        } else {
            keys.addAll(values);
        }
        this.map.put(sign, keys);
    }

    /**
     * 重组map
     */
    public void regroup() {
        if (this.map == null || this.map.isEmpty()) {
            return;
        }
        int maxSign = this.map.keySet().stream().max(Integer::compareTo).orElse(0);

        int log2 = Double.valueOf(Math.floor(Math.log(maxSign) / Math.log(2))).intValue();
        Map<Integer, Set<Key>> newMap = new HashMap<>();
        for (int i = 0; i <= log2; i++) {
            int sign = 1 << i;
            for (Entry<Integer, Set<Key>> entry : this.map.entrySet()) {
                if (sign == (entry.getKey() & sign)) {
                    Set<Key> keys = newMap.get(sign);
                    if (keys == null) {
                        keys = entry.getValue();
                    } else {
                        keys.addAll(entry.getValue());
                    }
                    newMap.put(sign, keys);
                }
            }
        }
        this.map = newMap;
    }

    public KeySignMap<Key> toKeySignMap() {
        return new KeySignMap<>(this);
    }
}
