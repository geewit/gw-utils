package io.geewit.core.utils.tree;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

/**
 * 标记过的树节点集合
 * @author geewit
 */
@Builder
@Setter
@Getter
public class KeySignMap<Key extends Serializable> implements Map<Key, Integer> {

    /**
     * Constructor uses an existing <code>Map</code> to store the values.
     * @param map The <code>Map</code> backing this <code>SimpleBindings</code>.
     * @throws NullPointerException if m is null
     */
    public KeySignMap(Map<Key, Integer> map) {
        if (map == null) {
            throw new NullPointerException();
        }
        this.map = map;
    }

    /**
     * Default constructor uses a <code>HashMap</code>.
     */
    public KeySignMap() {
        this(new HashMap<>());
    }

    public <S extends NodeSign<Key>> KeySignMap(Collection<S> nodeSigns) {
        this.map = new HashMap<>();
        if (nodeSigns == null || nodeSigns.isEmpty()) {
            return;
        }
        for (S nodeSign : nodeSigns) {
            if (nodeSign.getSign() == null) {
                continue;
            }
            Integer existSign = this.map.get(nodeSign.getId());
            if (existSign == null) {
                existSign = nodeSign.getSign();
            } else {
                existSign = existSign | nodeSign.getSign();
            }
            this.map.put(nodeSign.getId(), existSign);
        }
    }

    public KeySignMap(SignKeysMap<Key> signKeysMap) {
        this.map = new HashMap<>();
        if (signKeysMap == null || signKeysMap.isEmpty()) {
            return;
        }
        for (Entry<Integer, Set<Key>> entry : signKeysMap.entrySet()) {
            for (Key key : entry.getValue()) {
                this.add(key, entry.getKey());
            }
        }
    }

    /**
     * The <code>Map</code> field stores the attributes.
     */
    private Map<Key, Integer> map;

    /** {@inheritDoc} */
    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified
     * key.  More formally, returns <tt>true</tt> if and only if
     * this map contains a mapping for a key <tt>k</tt> such that
     * <tt>(key==null ? k==null : key.equals(k))</tt>.  (There can be
     * at most one such mapping.)
     *
     * @param key key whose presence in this map is to be tested.
     * @return <tt>true</tt> if this map contains a mapping for the specified
     *         key.
     *
     * @throws NullPointerException if key is null
     * @throws ClassCastException if key is not String
     * @throws IllegalArgumentException if key is empty String
     */
    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    /** {@inheritDoc} */
    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    /**
     * Returns the value to which this map maps the specified key.  Returns
     * <tt>null</tt> if the map contains no mapping for this key.  A return
     * value of <tt>null</tt> does not <i>necessarily</i> indicate that the
     * map contains no mapping for the key; it's also possible that the map
     * explicitly maps the key to <tt>null</tt>.  The <tt>containsKey</tt>
     * operation may be used to distinguish these two cases.
     *
     * <p>More formally, if this map contains a mapping from a key
     * <tt>k</tt> to a value <tt>v</tt> such that <tt>(key==null ? k==null :
     * key.equals(k))</tt>, then this method returns <tt>v</tt>; otherwise
     * it returns <tt>null</tt>.  (There can be at most one such mapping.)
     *
     * @param key key whose associated value is to be returned.
     * @return the value to which this map maps the specified key, or
     *         <tt>null</tt> if the map contains no mapping for this key.
     *
     * @throws NullPointerException if key is null
     * @throws ClassCastException if key is not String
     * @throws IllegalArgumentException if key is empty String
     */
    @Override
    public Integer get(Object key) {
        return map.get(key);
    }

    /**
     * Sets the specified key/value in the underlying <code>map</code> field.
     *
     * @param key Name of value
     * @param value Value to set.
     *
     * @return Previous value for the specified key.  Returns null if key was previously
     * unset.
     *
     * @throws NullPointerException if the name is null.
     * @throws IllegalArgumentException if the name is empty.
     */
    @Override
    public Integer put(Key key, Integer value) {
        return map.put(key, value);
    }

    @Override
    public Integer remove(Object key) {
        return map.remove(key);
    }

    /**
     * <code>putAll</code> is implemented using <code>Map.putAll</code>.
     *
     * @param toMerge The <code>Map</code> of values to add.
     *
     * @throws NullPointerException
     *         if toMerge map is null or if some key in the map is null.
     * @throws IllegalArgumentException
     *         if some key in the map is an empty String.
     */
    @Override
    public void putAll(Map<? extends Key, ? extends Integer> toMerge) {
        if (toMerge == null) {
            throw new NullPointerException("toMerge map is null");
        }
        for (Map.Entry<? extends Key, ? extends Integer> entry : toMerge.entrySet()) {
            Key key = entry.getKey();
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
    public Set<Key> keySet() {
        return map.keySet();
    }

    /** {@inheritDoc} */
    @Override
    public Collection<Integer> values() {
        return map.values();
    }

    /** {@inheritDoc} */
    @Override
    public Set<Entry<Key, Integer>> entrySet() {
        return map.entrySet();
    }

    public void add(Key key, Integer sign) {
        if (key == null) {
            return;
        }
        Integer existSign = this.map.get(key);
        if (existSign == null) {
            existSign = sign;
        } else {
            existSign |= sign;
        }
        this.map.put(key, existSign);
    }
}
