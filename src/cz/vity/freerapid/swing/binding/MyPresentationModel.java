/*
 * Copyright (c) 2002-2007 JGoodies Karsten Lentzsch. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JGoodies Karsten Lentzsch nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package cz.vity.freerapid.swing.binding;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.beans.*;
import com.jgoodies.binding.internal.IPresentationModel;
import com.jgoodies.binding.value.*;
import com.jgoodies.common.base.Objects;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

import static com.jgoodies.common.base.Preconditions.checkArgument;
import static com.jgoodies.common.base.Preconditions.checkNotNull;

/**
 * The standard base class to implement the <em>Presentation Model</em> pattern,
 * that represents the state and behavior of a presentation independently
 * of the GUI components used in the interface. This
 * <a href="http://martinfowler.com/eaaDev/PresentationModel.html">pattern</a>
 * is described in Martin Fowler's upcoming
 * <a href="http://martinfowler.com/eaaDev/">addition</a>
 * to his "Patterns of Enterprise Application Architecture". More details
 * around this implementation of the Presentation Model pattern and a 3-tier
 * Swing client architecture with a presentation model layer can be found in
 * the <a href="http://www.jgoodies.com/articles/binding.pdf">JGoodies
 * Binding presentation</a>. This architecture is supported
 * by the JGoodies Binding library.<p>
 *
 * This class minimizes the effort required to bind, edit,
 * buffer, and observe the bound properties of an exchangeable bean.
 * Therefore it provides five groups of features that are described below:<ol>
 * <li>adapt bean properties,
 * <li>change the adapted bean,
 * <li>buffer values,
 * <li>observe the buffering state, and
 * <li>track changes in adapted bean properties.
 * </ol><p>
 *
 * Typically this class will be extended to add custom models, Actions,
 * presentation logic, model operations and other higher-level behavior.
 * However, in simple cases you can use this class as-is.
 * Several methods are intended to be used as-is and a typical subclass
 * should not modify them. For example #isChanged, #isBuffering,
 * #getBean, #setBean, #getBeanChannel, #getModel, #getBufferedModel,
 * #getTriggerChannel, #setTriggerChannel, #triggerCommit and #triggerFlush.<p>
 *
 * <strong>Adapting Bean Properties</strong><br>
 * The method {@link #getModel(String)} vends ValueModels that adapt
 * a bound bean property of an exchangeable bean. These ValueModels will be
 * requested from an underlying BeanAdapter.
 * To get such a model you specify the name of the bean property.
 * All properties adapted must be read-write and must comply with
 * the Java Bean coding conventions.
 * In case you need to adapt a read-only or write-only property,
 * or if the bean uses custom names for the reader and writer,
 * use {@link #getModel(String, String, String)}.
 * Also note that you must not mix calls to these methods for the same
 * property name. For details see the JavaDoc class comment in
 * {@link com.jgoodies.binding.beans.BeanAdapter}.<p>
 *
 * <strong>Changing the Adapted Bean</strong><br>
 * The adapted bean is not stored in this PresentationModel.
 * Instead it is held by a ValueModel, the <em>bean channel</em>
 * - just as in the PropertyAdapter and BeanAdapter.
 * This indirection enables you to manage the adapted bean outside
 * of this PresentationModel, and it enables you to share bean channels
 * between multiple PresentationModels, PropertyAdapters, and BeanAdapters.
 * The bean channel is used by all adapting models created
 * by the factory methods {@code #getModel}.
 * You can get and set the current bean by means of {@code #getBean}
 * and {@code #setBean}. Or you can set a new value to the bean channel.<p>
 *
 * PresentationModel fires three PropertyChangeEvents if the bean changes:
 * <i>beforeBean</i>, <i>bean</i> and <i>afterBean</i>. This is useful
 * when sharing a bean channel and you must perform an operation before
 * or after other listeners handle a bean change. Since you cannot rely
 * on the order listeners will be notified, only the <i>beforeBean</i>
 * and <i>afterBean</i> events are guaranteed to be fired before and
 * after the bean change is fired.
 * Note that {@code #getBean()} returns the new bean before
 * any of these three PropertyChangeEvents is fired. Therefore listeners
 * that handle these events must use the event's old and new value
 * to determine the old and new bean.
 * The order of events fired during a bean change is:<ol>
 * <li>the bean channel fires a <i>value</i> change,
 * <li>this model fires a <i>beforeBean</i> change,
 * <li>this model fires the <i>bean</i> change,
 * <li>this model fires an <i>afterBean</i> change.
 * </ol>
 *
 * <strong>Buffering Values</strong><br>
 * At the core of this feature are the methods {@link #getBufferedModel(String)}
 * that vend BufferedValueModels that wrap an adapted bean property.
 * The buffer can be committed or flushed using {@code #triggerCommit}
 * and {@code #triggerFlush} respectively.<p>
 *
 * The trigger channel is provided as a bound Java bean property
 * <em>triggerChannel</em> that must be a non-{@code null}
 * {@code ValueModel} with values of type {@code Boolean}.
 * Attempts to read or write other value types may be rejected
 * with runtime exceptions.
 * By default the trigger channel is initialized as an instance of
 * {@code Trigger}. As an alternative it can be set in the constructor.<p>
 *
 * <strong>Observing the Buffering State</strong><br>
 * This class also provides support for observing the buffering state
 * of the BufferedValueModels created with this model. The buffering state
 * is useful for UI actions and operations that are enabled or disabled
 * if there are pending changes, for example on OK or APPLY button.
 * API users can request the buffering state via {@code #isBuffering}
 * and can observe the bound property <em>buffering</em>.<p>
 *
 * <strong>Tracking Changes in the Adapted Bean</strong><br>
 * PresentationModel provides support for observing bean property changes
 * and it tracks all changes to report the overall changed state.
 * The latter is useful to detect whether the bean has changed at all,
 * for example to mark the bean as dirty, so it will be updated in a database.
 * API users can request the changed state via {@code #isChanged}
 * and can observe the bound property <em>changed</em>.
 * If you want to track changes of other ValueModels, bean properties,
 * or of submodels, register them using {@code #observeChanged}.
 * To reset the changed state invoke {@code #resetChanged}.
 * In case you track the changed state of submodels you should override
 * {@code #resetChanged} to reset the changed state in these submodels.<p>
 *
 * The changed state changes once only (from false to true). If you need
 * instant notifications about changes in the properties of the target bean,
 * you can register PropertyChangeListeners with this model. This is useful
 * if you change the bean and don't want to move your listeners from one bean
 * to the other. And it's useful if you want to observe multiple bean
 * properties at the same time. These listeners are managed by the method set
 * {@code #addBeanPropertyChangeListener} and
 * {@code #removeBeanPropertyChangeListener}.
 * Listeners registered via these methods will be removed
 * from the old bean before the bean changes and will be re-added after
 * the new bean has been set. Therefore these listeners will be notified
 * about changes only if the current bean changes a property. They won't be
 * notified if the bean changes - and in turn the property value. If you want
 * to observes property changes caused by bean changes too, register with
 * the adapting ValueModel as returned by {@code #getModel(String)}.<p>
 *
 * <strong>Instance Creation</strong><br>
 * PresentationModel can be instantiated using four different constructors:
 * you can specify the target bean directly, or you can provide a
 * <em>bean channel</em> to access the bean indirectly.
 * In the latter case you specify a {@code ValueModel}
 * that holds the bean that in turn holds the adapted property.
 * In both cases the target bean is accessed indirectly through
 * the bean channel. In both cases you can specify a custom trigger channel,
 * or you can use a default trigger channel.<p>
 *
 * <strong>Note:</strong> This PresentationModel provides bound bean properties
 * and you can register and unregister PropertyChangeListers as usual using
 * {@code #addPropertyChangeListener} and
 * {@code #removePropertyChangeListener}. Do not mix up
 * the model listeners with the listeners registered with the bean.<p>
 *
 * <strong>Warning:</strong> PresentationModels register a
 * PropertyChangeListener with the target bean. Hence, a bean has a reference
 * to all PresentationModels that hold it as target bean. To avoid memory leaks
 * it is recommended to remove this listener if the bean lives much longer
 * than the PresentationModel, enabling the garbage collector to remove
 * the PresentationModel.
 * Setting a PresentationModel's target bean to null removes this listener,
 * which in turn clears the reference from the bean to the PresentationModel.
 * To do so, you can call {@code setBean(null)} or set the
 * bean channel's value to null.
 * As an alternative you can use event listener lists in your beans
 * that implement references with {@code WeakReference}.
 * Setting the bean to null has side effects, which is fine in most cases.
 * However, you can release all listeners by calling {@code #release}.<p>
 *
 * TODO: Further improve the class comment.<p>
 *
 * TODO: Consider adding a feature to ensure that update notifications
 * are performed in the event dispatch thread. In case the adapted bean
 * is changed in a thread other than the event dispatch thread, such
 * a feature would help complying with Swing's single thread rule.
 * The feature could be implemented by an extended PropertyChangeSupport.<p>
 *
 * TODO: I plan to improve the support for adapting beans that do not fire
 * PropertyChangeEvents. This affects the classes PropertyAdapter, BeanAdapter,
 * and PresentationModel. Basically the PropertyAdapter and the BeanAdapter's
 * internal SimplePropertyAdapter's shall be able to optionally self-fire
 * a PropertyChangeEvent in case the bean does not. There are several
 * downsides with self-firing events compared to bound bean properties.
 * See <a href="https://binding.dev.java.net/issues/show_bug.cgi?id=49">Issue
 * 49</a> for more information about the downsides.<p>
 *
 * The observeChanges constructor parameter shall be replaced by a more
 * fine-grained choice to not observe (former observeChanges=false),
 * to observe bound properties (former observeChanges=true), and a new
 * setting for self-firing PropertyChangeEvents if a value is set.
 * The latter case may be further split up to specify how the
 * self-fired PropertyChangeEvent is created:
 * <ol>
 * <li>oldValue=null, newValue=null
 * <li>oldValue=null, newValue=the value set
 * <li>oldValue=value read before the set, newValue=the value set
 * <li>oldValue=value read before the set, newValue=value read after the set
 * </ol>
 *
 * @author Karsten Lentzsch
 * @version $Revision: 1.26 $
 *
 * @see     com.jgoodies.binding.beans.BeanAdapter
 * @see     com.jgoodies.binding.value.ValueModel
 * @see     com.jgoodies.binding.beans.PropertyAdapter
 * @see     com.jgoodies.binding.value.Trigger
 *
 * @param <B>  the type of the bean managed by this PresentationModel
 */
public class MyPresentationModel<B> extends Model implements IPresentationModel<B> {


    // Property Names *********************************************************

    /**
     * The property name used in the PropertyChangeEvent that is fired
     * before the <em>bean</em> property fires its PropertyChangeEvent.
     * Useful to perform an operation before listeners that handle the
     * bean change are notified. See also the class comment.
     */
    public static final String PROPERTY_BEFORE_BEAN = "beforeBean";

    /**
     * The name of the read-write bound property that holds the target bean.
     *
     * @see #getBean()
     * @see #setBean(Object)
     */
    public static final String PROPERTY_BEAN = "bean";

    /**
     * The property name used in the PropertyChangeEvent that is fired
     * after the <em>bean</em> property fires its PropertyChangeEvent.
     * Useful to perform an operation after listeners that handle the
     * bean change are notified. See also the class comment.
     */
    public static final String PROPERTY_AFTER_BEAN = "afterBean";

    /**
     * The name of the read-write bound bean property for the
     * trigger channel that is shared by all PropertyAdapters
     * that are created via {@code #getBufferedModel}.
     *
     * @see #getTriggerChannel()
     * @see #setTriggerChannel(ValueModel)
     * @see #getBufferedModel(String)
     */
    public static final String PROPERTY_TRIGGERCHANNEL = "triggerChannel";

    /**
     * The name of the read-only bound bean property that indicates
     * whether one of the buffered models is buffering.
     *
     * @see #isBuffering()
     * @see #getBufferedModel(String)
     */
    public static final String PROPERTY_BUFFERING = "buffering";

    /**
     * The name of the read-only bound bean property that
     * indicates whether one of the observed models has changed.
     *
     * @see #isChanged()
     * @see #resetChanged()
     * @see #observeChanged(ValueModel)
     * @see #observeChanged(Object, String)
     */
    public static final String PROPERTY_CHANGED = "changed";


    /**
     * The property name used in the PropertyChangeEvent that is fired
     * before the <em>bean</em> property fires its PropertyChangeEvent.
     * Useful to perform an operation before listeners that handle the
     * bean change are notified. See also the class comment.
     *
     * @deprecated Replaced by {@link #PROPERTY_BEFORE_BEAN}
     */
    @Deprecated
    public static final String PROPERTYNAME_BEFORE_BEAN = PROPERTY_BEFORE_BEAN;

    /**
     * The name of the read-write bound property that holds the target bean.
     *
     * @see #getBean()
     * @see #setBean(Object)
     *
     * @deprecated Replaced by {@link #PROPERTY_BEAN}
     */
    @Deprecated
    public static final String PROPERTYNAME_BEAN = PROPERTY_BEAN;

    /**
     * The property name used in the PropertyChangeEvent that is fired
     * after the <em>bean</em> property fires its PropertyChangeEvent.
     * Useful to perform an operation after listeners that handle the
     * bean change are notified. See also the class comment.
     *
     * @deprecated Replaced by {@link #PROPERTY_AFTER_BEAN}
     */
    @Deprecated
    public static final String PROPERTYNAME_AFTER_BEAN = PROPERTY_AFTER_BEAN;

    /**
     * The name of the read-write bound bean property for the
     * trigger channel that is shared by all PropertyAdapters
     * that are created via {@code #getBufferedModel}.
     *
     * @see #getTriggerChannel()
     * @see #setTriggerChannel(ValueModel)
     * @see #getBufferedModel(String)
     * @deprecated Replaced by {@link #PROPERTY_TRIGGERCHANNEL}
     */
    @Deprecated
    public static final String PROPERTYNAME_TRIGGERCHANNEL = PROPERTY_TRIGGERCHANNEL;

    /**
     * The name of the read-only bound bean property that indicates
     * whether one of the buffered models is buffering.
     *
     * @see #isBuffering()
     * @see #getBufferedModel(String)
     * @deprecated Replaced by {@link #PROPERTY_BUFFERING}
     */
    @Deprecated
    public static final String PROPERTYNAME_BUFFERING = PROPERTY_BUFFERING;

    /**
     * The name of the read-only bound bean property that
     * indicates whether one of the observed models has changed.
     *
     * @see #isChanged()
     * @see #resetChanged()
     * @see #observeChanged(ValueModel)
     * @see #observeChanged(Object, String)
     * @deprecated Replaced by {@link #PROPERTY_CHANGED}
     */
    @Deprecated
    public static final String PROPERTYNAME_CHANGED = PROPERTY_CHANGED;


    // Fields *****************************************************************

    /**
     * Refers to the BeanAdapter that provides all underlying behavior
     * to vend adapting ValueModels, track bean changes, and to register
     * with bound bean properties.
     */
    private final BeanAdapter<B> beanAdapter;

    /**
     * Holds a three-state trigger channel that can be used to trigger
     * commit and reset events in instances of BufferedValueModel.
     * The trigger value is changed to true in {@code #triggerCommit}
     * and is changed to false in {@code #triggerFlush}.<p>
     *
     * The trigger channel is initialized as a {@code Trigger}
     * but may be replaced by any other ValueModel that accepts booleans.
     *
     * @see #getTriggerChannel()
     * @see #setTriggerChannel(ValueModel)
     * @see #getBufferedModel(String)
     */
    private ValueModel triggerChannel;

    /**
     * Maps property names to instances of the inner class WrappedBuffer.
     * These hold a BufferedValueModel associated with the property name,
     * as well as an optional getter and setter name. These accessor names
     * are used to check that multiple calls to {@code #getBufferedModel}
     * use the same getter and setter for a given property name.<p>
     *
     * The indirectly stored BufferedValueModel are checked whenever
     * the buffering state is updated. And these model's trigger channel
     * is updated when the PresentationModel gets a new trigger channel.
     *
     * @see #getBufferedModel(String)
     * @see #getBufferedModel(String, String, String)
     * @see #isBuffering()
     * @see #setTriggerChannel(ValueModel)
     */
    private final Map<String, WrappedBuffer> wrappedBuffers;

    /**
     * Listens to value changes and validates this model.
     * The validation result is available in the validationResultHolder.<p>
     *
     * Also listens to changes of the <em>buffering</em> property in
     * {@code BufferedValueModel}s and updates the buffering state
     * - if necessary.
     */
    private final PropertyChangeListener bufferingUpdateHandler;

    /**
     * Indicates whether a registered buffered model has a pending change,
     * in other words whether any of the values has been edited or not.
     */
    private boolean buffering = false;


    /**
     * Listens to property changes and updates the <em>changed</em> property.
     */
    private final PropertyChangeListener changedUpdateHandler;

    /**
     * Indicates whether a registered model has changed.
     */
    private boolean changed = false;


    /**
     * Maps property names to instances of ComponentValueModel.
     * Used to ensure that multiple calls to #getComponentModel
     * return the same instance.
     *
     * @see #getComponentModel(String)
     */
    private final Map<String, AbstractWrappedValueModel> componentModels;

    /**
     * Maps property names to instances of ComponentValueModel.
     * Used to ensure that multiple calls to #getBufferedComponentModel
     * return the same instance.
     *
     * @see #getBufferedComponentModel(String)
     */
    private final Map<String, AbstractWrappedValueModel> bufferedComponentModels;


    // Instance Creation ******************************************************

    /**
     * Constructs a PresentationModel where the initial bean is {@code null}.<p>
     *
     * Installs a default bean channel that checks the identity not equity
     * to ensure that listeners are unregistered properly if the old and
     * new bean are equal but not the same.<p>
     *
     * Installs a Trigger as initial trigger channel.
     *
     * @since 2.6.1
     */
    public MyPresentationModel() {
        this((B) null);
    }


    /**
     * Constructs a PresentationModel that adapts properties of the given bean.<p>
     *
     * Installs a default bean channel that checks the identity not equity
     * to ensure that listeners are unregistered properly if the old and
     * new bean are equal but not the same.<p>
     *
     * Installs a Trigger as initial trigger channel.
     *
     * @param bean   the bean that holds the properties to adapt
     * @throws PropertyUnboundException  if the {@code bean} does not
     *     provide a pair of methods to register a PropertyChangeListener
     */
    public MyPresentationModel(B bean) {
        this(new ValueHolder(bean, true));
    }


    /**
     * Constructs a PresentationModel on the given bean using the given
     * trigger channel. The bean provides the properties to adapt.<p>
     *
     * Installs a default bean channel that checks the identity not equity
     * to ensure that listeners are unregistered properly if the old and
     * new bean are equal but not the same.<p>
     *
     * The trigger channel is shared by all buffered models that are created
     * using {@code #getBufferedModel}.
     * It can be replaced by any other Boolean ValueModel later.
     * Note that PresentationModel observes trigger value changes,
     * not value state. Therefore you must ensure that customer triggers
     * report value changes when asked to commit or flush. See the
     * Trigger implementation for an example.
     *
     * @param bean           the bean that holds the properties to adapt
     * @param triggerChannel the ValueModel that triggers commit and flush events
     */
    public MyPresentationModel(
            B bean,
            ValueModel triggerChannel) {
        this(new ValueHolder(bean, true), triggerChannel);
    }


    /**
     * Constructs a PresentationModel on the given bean channel. This channel
     * holds a bean that in turn holds the properties to adapt.<p>
     *
     * It is strongly recommended that the bean channel checks the identity
     * not equity. This ensures that listeners are unregistered properly if
     * the old and new bean are equal but not the same.<p>
     *
     * The trigger channel is initialized as a {@code Trigger}.
     * It may be replaced by any other Boolean ValueModel later.
     * Note that PresentationModel observes trigger value changes,
     * not value state. Therefore you must ensure that customer triggers
     * report value changes when asked to commit or flush. See the
     * Trigger implementation for an example.
     *
     * @param beanChannel   the ValueModel that holds the bean
     *
     * @throws PropertyUnboundException  if the {@code bean} does not
     *     provide a pair of methods to register a PropertyChangeListener
     */
    public MyPresentationModel(ValueModel beanChannel) {
        this(beanChannel, new Trigger());
    }


    /**
     * Constructs a PresentationModel on the given bean channel using the given
     * trigger channel. The bean channel holds a bean that in turn holds
     * the properties to adapt.<p>
     *
     * It is strongly recommended that the bean channel checks the identity
     * not equity. This ensures that listeners are unregistered properly if
     * the old and new bean are equal but not the same.<p>
     *
     * The trigger channel is shared by all buffered
     * models that are created using {@code #buffer}.
     * It can be replaced by any other Boolean ValueModel later.
     * Note that PresentationModel observes trigger value changes,
     * not value state. Therefore you must ensure that customer triggers
     * report value changes when asked to commit or flush. See the
     * Trigger implementation for an example.
     *
     * @param beanChannel    the ValueModel that holds the bean
     * @param triggerChannel the ValueModel that triggers commit and flush events
     */
    public MyPresentationModel(
            ValueModel beanChannel,
            ValueModel triggerChannel) {
        this.beanAdapter = createBeanAdapter(beanChannel);
        this.triggerChannel = triggerChannel;
        this.wrappedBuffers = new HashMap<String, WrappedBuffer>();
        this.componentModels = new HashMap<String, AbstractWrappedValueModel>();
        this.bufferedComponentModels = new HashMap<String, AbstractWrappedValueModel>();
        this.bufferingUpdateHandler = new BufferingStateHandler();
        this.changed = false;
        this.changedUpdateHandler = new UpdateHandler();

        beanAdapter.addPropertyChangeListener(new BeanChangeHandler());

        // By default we observe changes in the bean.
        observeChanged(beanAdapter, BeanAdapter.PROPERTY_CHANGED);
    }


    /**
     *  Creates and returns a BeanAdapter for the given bean channel.
     *  For compatibility with the 1.0.x, 1.1.x, and 1.2.x series,
     *  this default implementation creates a BeanAdapter that always observes
     *  the bean. Subclasses may override to observe only observable beans.<p>
     *
     *  Here's an example code for a custom implementation:
     *  <pre>
     *  boolean observe =
     *        (beanChannel == null)
     *     || (beanChannel.getValue() == null)
     *     || BeanUtils.supportsBoundProperties((beanChannel.getValue().getClass());
     *  return new BeanAdapter(beanChannel, observe);
     *  </pre><p>
     *
     *  A future implementation shall return a BeanAdapter-like interface,
     *  not a BeanAdapter.
     *
     *  @param beanChannel the ValueModel that holds the bean
     *  @return the created bean adapter
     *  @since 1.3
     */
    protected BeanAdapter<B> createBeanAdapter(ValueModel beanChannel) {
        return new BeanAdapter<B>(beanChannel, true);
    }


    // Managing the Target Bean **********************************************

    /**
     * Returns the ValueModel that holds the bean that in turn holds
     * the adapted properties. This bean channel is shared by the
     * PropertyAdapters created by the factory methods
     * {@code #getModel} and {@code #getBufferedModel}.
     *
     * @return the ValueModel that holds the bean that in turn
     *     holds the adapted properties
     *
     * @see #getBean()
     * @see #setBean(Object)
     */
    public ValueModel getBeanChannel() {
        return beanAdapter.getBeanChannel();
    }


    /**
     * Returns the bean that holds the adapted properties. This bean
     * is the bean channel's content.
     *
     * @return the bean that holds the adapted properties
     *
     * @see #setBean(Object)
     * @see #getBeanChannel()
     */
    public B getBean() {
        return (B) getBeanChannel().getValue();
    }


    /**
     * Sets a new bean as content of the bean channel.
     * All adapted properties will reflect this change.
     *
     * @param newBean   the new bean
     *
     * @see #getBean()
     * @see #getBeanChannel()
     */
    public void setBean(B newBean) {
        getBeanChannel().setValue(newBean);
    }


    /**
     * The underlying BeanAdapter is about to change the bean.
     * Allows to perform actions before the bean change happens.
     * For example you can remove listeners that shall not be notified
     * if adapted properties change just because of the bean change.
     * Or you can reset values, set fields to {@code null} etc.<p>
     *
     * The default behavior fires a PropertyChangeEvent for property
     * {@code #PROPERTY_BEFORE_BEAN}.
     * <strong>Note:</strong> Subclasses that override this method
     * must invoke super or perform the same behavior.<p>
     *
     * This method is invoked by the BeanChangeHandler listening to the
     * <em>beforeBean</em> non-readable property of the BeanAdapter.
     *
     * @param oldBean  the bean before the change
     * @param newBean  the bean that will be adapted after the change
     *
     * @see #afterBeanChange(Object, Object)
     * @see #PROPERTY_BEFORE_BEAN
     * @see #PROPERTY_BEAN
     * @see #PROPERTY_AFTER_BEAN
     * @see BeanAdapter
     */
    public void beforeBeanChange(B oldBean, B newBean) {
        firePropertyChange(PROPERTY_BEFORE_BEAN, oldBean, newBean, true);
    }


    /**
     * The underlying BeanAdapter has changed the target bean.
     * Allows to perform actions after the bean changed.
     * For example you can re-add listeners that were removed in
     * {@code #beforeBeanChange}. Or you can reset values,
     * reset custom changed state, set fields to {@code null} etc.<p>
     *
     * The default behavior resets the change tracker's <em>changed</em> state
     * and fires a PropertyChangeEvent for the property
     * {@code #PROPERTY_AFTER_BEAN}.
     * <strong>Note:</strong> Subclasses that override this method
     * must invoke super or perform the same behavior.<p>
     *
     * This method is invoked by the BeanChangeHandler listening to the
     * <em>afterBean</em> non-readable property of the BeanAdapter.
     *
     * @param oldBean  the bean that was adapted before the change
     * @param newBean  the bean that is already the new target bean
     *
     * @see #beforeBeanChange(Object, Object)
     * @see #PROPERTY_BEFORE_BEAN
     * @see #PROPERTY_BEAN
     * @see #PROPERTY_AFTER_BEAN
     * @see BeanAdapter
     */
    public void afterBeanChange(B oldBean, B newBean) {
        setChanged(false);
        firePropertyChange(PROPERTY_AFTER_BEAN, oldBean, newBean, true);
    }


    // Accessing Property Values **********************************************

    /**
     * Returns the value of specified bean property, {@code null}
     * if the current bean is {@code null}.<p>
     *
     * This operation is supported only for readable bean properties.
     *
     * @param propertyName  the name of the property to be read
     * @return the value of the adapted bean property, null if the bean is null
     *
     * @throws NullPointerException           if the property name is null
     * @throws UnsupportedOperationException  if the property is write-only
     * @throws PropertyNotFoundException      if the property could not be found
     * @throws PropertyAccessException        if the value could not be read
     *
     * @since 1.1
     */
    public Object getValue(String propertyName) {
        return beanAdapter.getValue(propertyName);
    }


    /**
     * Sets the given new value for the specified bean property. Does nothing
     * if this adapter's bean is {@code null}. If the setter associated
     * with the propertyName throws a PropertyVetoException, it is silently
     * ignored.<p>
     *
     * Notifies the associated value change listeners if the bean reports
     * a property change. Note that a bean may suppress PropertyChangeEvents
     * if the old and new value are the same, or if the old and new value
     * are equal.<p>
     *
     * This operation is supported only for writable bean properties.
     *
     * @param propertyName   the name of the property to set
     * @param newValue       the value to set
     *
     * @throws NullPointerException           if the property name is null
     * @throws UnsupportedOperationException  if the property is read-only
     * @throws PropertyNotFoundException      if the property could not be found
     * @throws PropertyAccessException        if the new value could not be set
     *
     * @since 1.1
     */
    public void setValue(String propertyName, Object newValue) {
        beanAdapter.setValue(propertyName, newValue);
    }


    /**
     * Sets a new value for the specified bean property. Does nothing if the
     * bean is {@code null}. If the setter associated with the propertyName
     * throws a PropertyVetoException, this methods throws the same exception.<p>
     *
     * Notifies the associated value change listeners if the bean reports
     * a property change. Note that a bean may suppress PropertyChangeEvents
     * if the old and new value are the same, or if the old and new value
     * are equal.<p>
     *
     * This operation is supported only for writable bean properties.
     *
     * @param propertyName   the name of the property to set
     * @param newValue       the value to set
     *
     * @throws NullPointerException           if the property name is null
     * @throws UnsupportedOperationException  if the property is read-only
     * @throws PropertyNotFoundException      if the property could not be found
     * @throws PropertyAccessException        if the new value could not be set
     * @throws PropertyVetoException          if the bean setter
     *     throws a PropertyVetoException
     *
     * @since 1.1
     */
    public void setVetoableValue(String propertyName, Object newValue) throws PropertyVetoException {
        beanAdapter.setVetoableValue(propertyName, newValue);
    }


    /**
     * Returns the value of specified buffered bean property.
     * It is a shorthand for writing
     * <pre>getBufferedModel(propertyName).getValue()</pre>
     * As a side-effect, this method may create a buffered model.
     *
     * @param propertyName  the name of the property to be read
     * @return the value of the adapted bean property, null if the bean is null
     *
     * @throws NullPointerException           if the property name is null
     * @throws UnsupportedOperationException  if the property is write-only
     * @throws PropertyNotFoundException      if the property could not be found
     * @throws PropertyAccessException        if the value could not be read
     *
     * @since 1.1
     */
    public Object getBufferedValue(String propertyName) {
        return getBufferedModel(propertyName).getValue();
    }


    /**
     * Buffers the given value for the specified bean property.
     * It is a shorthand for writing
     * <pre>getBufferedModel(propertyName).setValue(newValue)</pre>
     * As a side-effect, this method may create a buffered model.
     *
     * @param propertyName   the name of the property to set
     * @param newValue       the value to set
     *
     * @throws NullPointerException           if the property name is null
     * @throws PropertyNotFoundException      if the property could not be found
     * @throws PropertyAccessException        if the new value could not be set
     *
     * @since 1.1
     */
    public void setBufferedValue(String propertyName, Object newValue) {
        getBufferedModel(propertyName).setValue(newValue);
    }


    // Factory Methods for Bound Models ***************************************

    /**
     * Looks up and lazily creates a ValueModel that adapts
     * the bound property with the specified name. Uses the
     * Bean introspection to look up the getter and setter names.<p>
     *
     * Subsequent calls to this method with the same property name
     * return the same ValueModel.<p>
     *
     * To prevent potential runtime errors it eagerly looks up
     * the associated PropertyDescriptor if the target bean is not null.<p>
     *
     * For each property name all calls to this method
     * and to {@code #getModel(String, String, String)} must use
     * the same getter and setter names. Attempts to violate this constraint
     * will be rejected with an IllegalArgumentException. Especially once
     * you've called this method you must not call
     * {@code #getModel(String, String, String)} with a non-null
     * getter or setter name. And vice versa, once you've called the latter
     * method with a non-null getter or setter name, you must not call
     * this method.<p>
     *
     * This method uses a return type of AbstractValueModel, not a ValueModel.
     * This makes the AbstractValueModel convenience type converters available,
     * which can significantly shrink the source code necessary to read and
     * write values from/to these models.
     *
     * @param propertyName   the name of the property to adapt
     * @return a ValueModel that adapts the property with the specified name
     *
     * @throws NullPointerException       if the property name is null
     * @throws PropertyNotFoundException  if the property could not be found
     * @throws IllegalArgumentException
     *     if {@code #getModel(String, String, String)} has been
     *     called before with the same property name and a non-null getter
     *     or setter name
     *
     * @see AbstractValueModel
     * @see BeanAdapter
     * @see #getModel(String, String, String)
     * @see #getBufferedModel(String)
     */
    public AbstractValueModel getModel(String propertyName) {
        return beanAdapter.getValueModel(propertyName);
    }


    /**
     * Looks up and lazily creates a ValueModel that adapts the bound property
     * with the given name. Unlike {@code #getModel(String)}
     * this method bypasses the Bean Introspection and uses the given getter
     * and setter names to setup the access to the adapted Bean property.<p>
     *
     * Subsequent calls to this method with the same parameters
     * will return the same ValueModel.<p>
     *
     * To prevent potential runtime errors this method eagerly looks up
     * the associated PropertyDescriptor if the target bean is not null.<p>
     *
     * For each property name all calls to this method
     * and to {@code #getModel(String)} must use the same
     * getter and setter names. Attempts to violate this constraint
     * will be rejected with an IllegalArgumentException. Especially
     * once you've called this method with a non-null getter or setter name,
     * you must not call {@code #getModel(String)}. And vice versa,
     * once you've called the latter method you must not call this method
     * with a non-null getter or setter name.<p>
     *
     * This method uses a return type of AbstractValueModel, not a ValueModel.
     * This makes the AbstractValueModel convenience type converters available,
     * which can significantly shrink the source code necessary to read and
     * write values from/to these models.
     *
     * @param propertyName   the name of the property to adapt
     * @param getterName     the name of the method that reads the value
     * @param setterName     the name of the method that sets the value
     * @return a ValueModel that adapts the property with the specified name
     *
     * @throws NullPointerException       if the property name is null
     * @throws PropertyNotFoundException  if the property could not be found
     * @throws IllegalArgumentException   if this method has been called before
     *     with the same property name and different getter or setter names
     *
     * @see AbstractValueModel
     * @see BeanAdapter
     * @see #getModel(String, String, String)
     * @see #getBufferedModel(String)
     */
    public AbstractValueModel getModel(String propertyName,
                                       String getterName,
                                       String setterName) {
        return beanAdapter.getValueModel(propertyName, getterName, setterName);
    }


    /**
     * Looks up and lazily creates a ComponentValueModel that adapts
     * the bound property with the specified name. Uses the standard
     * Bean introspection to look up the getter and setter names.<p>
     *
     * Subsequent calls to this method with the same property name
     * return the same ComponentValueModel.<p>
     *
     * To prevent potential runtime errors it eagerly looks up
     * the associated PropertyDescriptor if the target bean is not null.<p>
     *
     * For each property name all calls to this method
     * and to {@code #getModel(String, String, String)} must use
     * the same getter and setter names. Attempts to violate this constraint
     * will be rejected with an IllegalArgumentException. Especially once
     * you've called this method you must not call
     * {@code #getModel(String, String, String)} with a non-null
     * getter or setter name. And vice versa, once you've called the latter
     * method with a non-null getter or setter name, you must not call
     * this method.<p>
     *
     * This returned ComponentValueModel provides convenience type converter
     * method from AbstractValueModel and allows to modify GUI state such as
     * enabled, visible, and editable in this presentation model.
     * This can significantly shrink the source code necessary to handle
     * GUI state changes.
     *
     * @param propertyName   the name of the property to adapt
     * @return a ValueModel that adapts the property with the specified name
     *
     * @throws NullPointerException       if the property name is null
     * @throws PropertyNotFoundException  if the property could not be found
     * @throws IllegalArgumentException
     *     if {@code #getModel(String, String, String)} has been
     *     called before with the same property name and a non-null getter
     *     or setter name
     *
     * @see ComponentValueModel
     * @see AbstractValueModel
     * @see BeanAdapter
     * @see #getModel(String, String, String)
     * @see #getBufferedModel(String)
     * @see Bindings#addComponentPropertyHandler(JComponent, ValueModel)
     *
     * @since 1.1
     */
    @Override
    public AbstractWrappedValueModel getComponentModel(String propertyName) {
        AbstractWrappedValueModel componentModel = componentModels.get(propertyName);
        if (componentModel == null) {
            AbstractValueModel model = getModel(propertyName);
            componentModel = new DefaultComponentValueModel(model);
            componentModels.put(propertyName, componentModel);
        }
        return componentModel;
    }


    // Factory Methods for Buffered Models ************************************

    /**
     * Looks up or creates a buffered adapter to the read-write property
     * with the given name on this PresentationModel's bean channel. Creates a
     * BufferedValueModel that wraps a ValueModel that adapts the bean property
     * with the specified name. The buffered model uses this PresentationModel's
     * trigger channel to listen for commit and flush events.<p>
     *
     * The created BufferedValueModel is stored in a Map. Hence
     * subsequent calls to this method with the same property name
     * return the same BufferedValueModel.<p>
     *
     * To prevent potential runtime errors this method eagerly looks up
     * the associated PropertyDescriptor if the target bean is not null.<p>
     *
     * For each property name all calls to this method
     * and to {@code #getBufferedModel(String, String, String)} must use
     * the same getter and setter names. Attempts to violate this constraint
     * will be rejected with an IllegalArgumentException. Especially once
     * you've called this method you must not call
     * {@code #getBufferedModel(String, String, String)} with a non-null
     * getter or setter name. And vice versa, once you've called the latter
     * method with a non-null getter or setter name, you must not call
     * this method.
     *
     * @param propertyName the name of the read-write property to adapt
     * @return a buffered adapter to the property with the given name
     *    on this model's bean channel using this model's trigger channel
     *
     * @throws NullPointerException       if the property name is null
     * @throws PropertyNotFoundException  if the property could not be found
     * @throws IllegalArgumentException
     *     if {@code #getBufferedModel(String, String, String)} has been
     *     called before with the same property name and a non-null getter
     *     or setter name
     *
     * @see BufferedValueModel
     * @see ValueModel
     * @see Trigger
     * @see BeanAdapter
     * @see #getModel(String)
     * @see #getBufferedModel(String, String, String)
     */
    public BufferedValueModel getBufferedModel(String propertyName) {
        return getBufferedModel(propertyName, null, null);
    }


    /**
     * Looks up or creates a buffered adapter to the read-write property
     * with the given name on this PresentationModel's bean channel using
     * the specified getter and setter name to read and write values. Creates
     * a {@code BufferedValueModel} that wraps a {@code ValueModel}
     * that adapts the bean property with the specified name.
     * The buffered model uses this PresentationModel's trigger channel
     * to listen for commit and flush events.<p>
     *
     * The created BufferedValueModel is stored in a Map so it can be
     * looked up if it is requested multiple times.<p>
     *
     * To prevent potential runtime errors this method eagerly looks up
     * the associated PropertyDescriptor if the target bean is not null.<p>
     *
     * For each property name all calls to this method
     * and to {@code #getBufferedModel(String)} must use the same
     * getter and setter names. Attempts to violate this constraint
     * will be rejected with an IllegalArgumentException. Especially
     * once you've called this method with a non-null getter or setter name,
     * you must not call {@code #getBufferedModel(String)}. And vice versa,
     * once you've called the latter method you must not call this method
     * with a non-null getter or setter name.
     *
     * @param propertyName   the name of the property to adapt
     * @param getterName     the name of the method that reads the value
     * @param setterName     the name of the method that sets the value
     * @return a buffered adapter to the property with the given name
     *    on this model's bean channel using this model's trigger channel
     *
     * @throws NullPointerException       if the property name is null
     * @throws PropertyNotFoundException  if the property could not be found
     * @throws IllegalArgumentException   if this method has been called before
     *     with the same property name and different getter or setter names
     *
     * @see BufferedValueModel
     * @see ValueModel
     * @see Trigger
     * @see BeanAdapter
     * @see #getModel(String)
     * @see #getBufferedModel(String)
     */
    public BufferedValueModel getBufferedModel(String propertyName,
                                               String getterName,
                                               String setterName) {
        WrappedBuffer wrappedBuffer = wrappedBuffers.get(propertyName);
        if (wrappedBuffer == null) {
            wrappedBuffer = new WrappedBuffer(
                    buffer(getModel(propertyName, getterName, setterName)),
                    getterName,
                    setterName);
            wrappedBuffers.put(propertyName, wrappedBuffer);
        } else {
            checkArgument(Objects.equals(getterName, wrappedBuffer.getterName)
                    && Objects.equals(setterName, wrappedBuffer.setterName),
                    "You must not invoke this method twice "
                            + "with different getter and/or setter names.");
        }
        return wrappedBuffer.buffer;
    }


    public BufferedValueModel getBufferedModel(MyPreferencesAdapter model) {
        WrappedBuffer wrappedBuffer = wrappedBuffers.get(model.getKey());
        if (wrappedBuffer == null) {
            wrappedBuffer = new WrappedBuffer(
                    buffer(model),
                    null,
                    null);
            wrappedBuffers.put(model.getKey(), wrappedBuffer);
        } else {
            throw new IllegalArgumentException(
                    "You must not invoke this method twice "
                            + "with different getter and/or setter names.");
        }
        return wrappedBuffer.buffer;
    }


    /**
     * Looks up or creates a buffered component adapter to the read-write
     * property with the given name on this PresentationModel's bean channel.
     * Creates a ComponentValueModel that wraps a BufferedValueModel that
     * in turn wraps a ValueModel that adapts the bean property with the
     * specified name. The buffered model uses this PresentationModel's
     * trigger channel to listen for commit and flush events.
     * The ComponentValueModel allows to set component state in this
     * presentation model.<p>
     *
     * The created ComponentValueModel is stored in a Map. Hence
     * subsequent calls to this method with the same property name
     * return the same ComponentValueModel.<p>
     *
     * To prevent potential runtime errors this method eagerly looks up
     * the associated PropertyDescriptor if the target bean is not null.<p>
     *
     * For each property name all calls to this method
     * and to {@code #getBufferedModel(String, String, String)} must use
     * the same getter and setter names. Attempts to violate this constraint
     * will be rejected with an IllegalArgumentException. Especially once
     * you've called this method you must not call
     * {@code #getBufferedModel(String, String, String)} with a non-null
     * getter or setter name. And vice versa, once you've called the latter
     * method with a non-null getter or setter name, you must not call
     * this method.
     *
     * @param propertyName the name of the read-write property to adapt
     * @return a ComponentValueModel that wraps a buffered adapter
     *    to the property with the given name
     *    on this model's bean channel using this model's trigger channel
     *
     * @throws NullPointerException       if the property name is null
     * @throws PropertyNotFoundException  if the property could not be found
     * @throws IllegalArgumentException
     *     if {@code #getBufferedModel(String, String, String)} has been
     *     called before with the same property name and a non-null getter
     *     or setter name
     *
     * @see ComponentValueModel
     * @see BufferedValueModel
     * @see ValueModel
     * @see Trigger
     * @see BeanAdapter
     * @see #getModel(String)
     * @see #getBufferedModel(String)
     * @see #getComponentModel(String)
     * @see Bindings#addComponentPropertyHandler(JComponent, ValueModel)
     *
     * @since 1.1
     */
    public AbstractWrappedValueModel getBufferedComponentModel(String propertyName) {
        AbstractWrappedValueModel bufferedComponentModel =
                bufferedComponentModels.get(propertyName);
        if (bufferedComponentModel == null) {
            AbstractValueModel model = getBufferedModel(propertyName);
            bufferedComponentModel = new DefaultComponentValueModel(model);
            bufferedComponentModels.put(propertyName, bufferedComponentModel);
        }
        return bufferedComponentModel;
    }


    /**
     * Wraps the given ValueModel with a BufferedValueModel that
     * uses this model's trigger channel to trigger commit and flush events.
     *
     * @param valueModel  the ValueModel to be buffered
     * @return a BufferedValueModel triggered by the model's trigger channel
     *
     * @see BufferedValueModel
     * @see ValueModel
     * @see Trigger
     * @see #getBufferedModel(String)
     */
    private BufferedValueModel buffer(ValueModel valueModel) {
        BufferedValueModel bufferedModel = new BufferedValueModel(
                valueModel, getTriggerChannel());
        bufferedModel.addPropertyChangeListener(BufferedValueModel.PROPERTY_BUFFERING, bufferingUpdateHandler);
        return bufferedModel;
    }


    // Accessing the Trigger Channel ******************************************

    /**
     * Returns a ValueModel that can be shared and used to trigger commit
     * and flush events in BufferedValueModels. The trigger channel's value
     * changes to true in {@code #triggerCommit} and it changes to false
     * in {@code #triggerFlush}.<p>
     *
     * This trigger channel is used to commit and flush values
     * in the BufferedValueModels returned by {@code #getBufferedModel}.
     *
     * @return this model's trigger channel
     *
     * @see BufferedValueModel
     * @see ValueModel
     * @see #setTriggerChannel(ValueModel)
     */
    public ValueModel getTriggerChannel() {
        return triggerChannel;
    }


    /**
     * Sets the given ValueModel as this model's new trigger channel.
     * Sets the new trigger channel in all existing BufferedValueModels
     * that have been created using {@code #getBufferedModel}.
     * Subsequent invocations of {@code #triggerCommit} and
     * {@code #triggerFlush} will trigger commit and flush events
     * using the new trigger channel.
     *
     * @param newTriggerChannel  the ValueModel to be set as
     *     this model's new trigger channel
     * @throws NullPointerException  if the new trigger channel is {@code null}
     *
     * @see BufferedValueModel
     * @see ValueModel
     * @see #getTriggerChannel()
     */
    public void setTriggerChannel(ValueModel newTriggerChannel) {
        checkNotNull(newTriggerChannel, "The trigger channel must not be null.");

        ValueModel oldTriggerChannel = getTriggerChannel();
        triggerChannel = newTriggerChannel;
        for (WrappedBuffer wrappedBuffer : wrappedBuffers.values()) {
            wrappedBuffer.buffer.setTriggerChannel(triggerChannel);
        }
        firePropertyChange(
                PROPERTY_TRIGGERCHANNEL,
                oldTriggerChannel,
                newTriggerChannel);
    }


    /**
     * Sets the trigger channel to true which in turn triggers commit
     * events in all BufferedValueModels that share this trigger.
     *
     * @see #triggerFlush()
     */
    public void triggerCommit() {
        if (Boolean.TRUE.equals(getTriggerChannel().getValue())) {
            getTriggerChannel().setValue(null);
        }
        getTriggerChannel().setValue(Boolean.TRUE);
    }


    /**
     * Sets the trigger channel to false which in turn triggers flush
     * events in all BufferedValueModels that share this trigger.
     *
     * @see #triggerCommit()
     */
    public void triggerFlush() {
        if (Boolean.FALSE.equals(getTriggerChannel().getValue())) {
            getTriggerChannel().setValue(null);
        }
        getTriggerChannel().setValue(Boolean.FALSE);
    }


    // Managing the Buffering State *******************************************

    /**
     * Answers whether any of the buffered models is buffering.
     * Useful to enable and disable UI actions and operations
     * that depend on the buffering state.
     *
     * @return true if any of the buffered models is buffering,
     *     false, if all buffered models write-through
     */
    public boolean isBuffering() {
        return buffering;
    }


    /**
     * Sets the buffering state to the specified value.
     *
     * @param newValue  the new buffering state
     */
    public void setBuffering(boolean newValue) {
        boolean oldValue = isBuffering();
        buffering = newValue;
        firePropertyChange(PROPERTY_BUFFERING, oldValue, newValue);
    }


    private void updateBufferingState(boolean latestBufferingStateChange) {
        if (buffering == latestBufferingStateChange) {
            return;
        }
        boolean nowBuffering = false;
        for (WrappedBuffer wrappedBuffer : wrappedBuffers.values()) {
            BufferedValueModel model = wrappedBuffer.buffer;
            nowBuffering = nowBuffering || model.isBuffering();
            if (!buffering && nowBuffering) {
                setBuffering(true);
                return;
            }
        }
        setBuffering(nowBuffering);
    }


    // Changed State *********************************************************

    /**
     * Answers whether one of the registered ValueModels has changed
     * since the changed state has been reset last time.<p>
     *
     * <strong>Note:</strong> Unlike {@code #resetChanged} this method
     * is not intended to be overridden by subclasses.
     * If you want to track changes of other ValueModels, bean properties, or
     * of submodels, register them by means of {@code #observeChanged}.
     * Overriding {@code #isChanged} to include the changed state
     * of submodels would return the correct changed value, but it would bypass
     * the change notification from submodels to this model.
     * Therefore submodels must be observed, which can be achieve using
     * {@code #observeChanged}.<p>
     *
     * To reset the changed state invoke {@code #resetChanged}.
     * In case you track the changed state of submodels override
     * {@code #resetChanged} to reset the changed state in these
     * submodels too.
     *
     * @return true if an observed property has changed since the last reset
     *
     * @see #observeChanged(ValueModel)
     * @see #observeChanged(Object, String)
     * @see #resetChanged()
     */
    public boolean isChanged() {
        return changed;
    }


    /**
     * Resets this model's changed state to {@code false}.
     * Therefore it resets the changed states of the change tracker
     * and the underlying bean adapter.<p>
     *
     * Subclasses may override this method to reset the changed state
     * of submodels. The overriding method must invoke this super behavior.
     * For example if you have a MainModel that is composed of
     * two submodels Submodel1 and Submodel2, you may write:
     * <pre>
     * public void resetChanged() {
     *     super.resetChanged();
     *     getSubmodel1().resetChanged();
     *     getSubmodel2().resetChanged();
     * }
     * </pre>
     *
     * @see #isChanged()
     * @see #observeChanged(ValueModel)
     * @see #observeChanged(Object, String)
     */
    public void resetChanged() {
        setChanged(false);
        beanAdapter.resetChanged();
    }


    protected void setChanged(boolean newValue) {
        boolean oldValue = isChanged();
        changed = newValue;
        firePropertyChange(PROPERTY_CHANGED, oldValue, newValue);
    }


    // Observing Changes in ValueModel and Bean Properties *******************

    /**
     * Observes the specified readable bound bean property in the given bean.
     *
     * @param bean           the bean to be observed
     * @param propertyName   the name of the readable bound bean property
     * @throws NullPointerException if the bean or propertyName is null
     * @throws PropertyNotBindableException if this class can't add
     *     the PropertyChangeListener from the bean
     *
     * @see #retractInterestFor(Object, String)
     * @see #observeChanged(ValueModel)
     */
    public void observeChanged(Object bean, String propertyName) {
        checkNotNull(bean, "The bean must not be null.");
        checkNotNull(propertyName, "The property name must not be null.");
        BeanUtils.addPropertyChangeListener(bean, propertyName, changedUpdateHandler);
    }


    /**
     * Observes value changes in the given ValueModel.
     *
     * @param valueModel   the ValueModel to observe
     * @throws NullPointerException if the valueModel is null
     *
     * @see #retractInterestFor(ValueModel)
     * @see #observeChanged(Object, String)
     */
    public void observeChanged(ValueModel valueModel) {
        checkNotNull(valueModel, "The ValueModel must not be null.");
        valueModel.addValueChangeListener(changedUpdateHandler);
    }


    /**
     * Retracts interest for the specified readable bound bean property
     * in the given bean.
     *
     * @param bean           the bean to be observed
     * @param propertyName   the name of the readable bound bean property
     * @throws NullPointerException if the bean or propertyName is null
     * @throws PropertyNotBindableException if this class can't remove
     *     the PropertyChangeListener from the bean
     *
     * @see #observeChanged(Object, String)
     * @see #retractInterestFor(ValueModel)
     */
    public void retractInterestFor(Object bean, String propertyName) {
        checkNotNull(bean, "The bean must not be null.");
        checkNotNull(propertyName, "The property name must not be null.");
        BeanUtils.removePropertyChangeListener(bean, propertyName, changedUpdateHandler);
    }


    /**
     * Retracts interest for value changes in the given ValueModel.
     *
     * @param valueModel   the ValueModel to observe
     * @throws NullPointerException if the valueModel is null
     *
     * @see #observeChanged(ValueModel)
     * @see #retractInterestFor(Object, String)
     */
    public void retractInterestFor(ValueModel valueModel) {
        checkNotNull(valueModel, "The ValueModel must not be null.");
        valueModel.removeValueChangeListener(changedUpdateHandler);
    }


    // Managing Bean Property Change Listeners *******************************

    /**
     * Adds a PropertyChangeListener to the list of bean listeners. The
     * listener is registered for all bound properties of the target bean.<p>
     *
     * The listener will be notified if and only if this BeanAdapter's current
     * bean changes a property. It'll not be notified if the bean changes.<p>
     *
     * If listener is {@code null}, no exception is thrown and
     * no action is performed.
     *
     * @param listener      the PropertyChangeListener to be added
     *
     * @see #removeBeanPropertyChangeListener(PropertyChangeListener)
     * @see #removeBeanPropertyChangeListener(String, PropertyChangeListener)
     * @see #addBeanPropertyChangeListener(String, PropertyChangeListener)
     * @see #getBeanPropertyChangeListeners()
     */
    public synchronized void addBeanPropertyChangeListener(
            PropertyChangeListener listener) {
        beanAdapter.addBeanPropertyChangeListener(listener);
    }


    /**
     * Removes a PropertyChangeListener from the list of bean listeners.
     * This method should be used to remove PropertyChangeListeners that
     * were registered for all bound properties of the target bean.<p>
     *
     * If listener is {@code null}, no exception is thrown and
     * no action is performed.
     *
     * @param listener      the PropertyChangeListener to be removed
     *
     * @see #addBeanPropertyChangeListener(PropertyChangeListener)
     * @see #addBeanPropertyChangeListener(String, PropertyChangeListener)
     * @see #removeBeanPropertyChangeListener(String, PropertyChangeListener)
     * @see #getBeanPropertyChangeListeners()
     */
    public synchronized void removeBeanPropertyChangeListener(
            PropertyChangeListener listener) {
        beanAdapter.removeBeanPropertyChangeListener(listener);
    }


    /**
     * Adds a PropertyChangeListener to the list of bean listeners for a
     * specific property. The specified property may be user-defined.<p>
     *
     * The listener will be notified if and only if this BeanAdapter's
     * current bean changes the specified property. It'll not be notified
     * if the bean changes. If you want to observe property changes and
     * bean changes, you may observe the ValueModel that adapts this property
     * - as returned by {@code #getModel(String)}.<p>
     *
     * Note that if the bean is inheriting a bound property, then no event
     * will be fired in response to a change in the inherited property.<p>
     *
     * If listener is {@code null}, no exception is thrown and
     * no action is performed.
     *
     * @param propertyName      one of the property names listed above
     * @param listener          the PropertyChangeListener to be added
     *
     * @see #removeBeanPropertyChangeListener(String, PropertyChangeListener)
     * @see #addBeanPropertyChangeListener(String, PropertyChangeListener)
     * @see #getBeanPropertyChangeListeners(String)
     */
    public synchronized void addBeanPropertyChangeListener(
            String propertyName,
            PropertyChangeListener listener) {
        beanAdapter.addBeanPropertyChangeListener(propertyName, listener);
    }


    /**
     * Removes a PropertyChangeListener from the listener list for a specific
     * property. This method should be used to remove PropertyChangeListeners
     * that were registered for a specific bound property.<p>
     *
     * If listener is {@code null}, no exception is thrown and
     * no action is performed.
     *
     * @param propertyName      a valid property name
     * @param listener          the PropertyChangeListener to be removed
     *
     * @see #addBeanPropertyChangeListener(String, PropertyChangeListener)
     * @see #removeBeanPropertyChangeListener(PropertyChangeListener)
     * @see #getBeanPropertyChangeListeners(String)
     */
    public synchronized void removeBeanPropertyChangeListener(
            String propertyName,
            PropertyChangeListener listener) {
        beanAdapter.removeBeanPropertyChangeListener(propertyName, listener);
    }


    // Requesting Listener Sets ***********************************************

    /**
     * Returns an array of all the property change listeners
     * registered on this component.
     *
     * @return all of this component's {@code PropertyChangeListener}s
     *         or an empty array if no property change
     *         listeners are currently registered
     *
     * @see #addBeanPropertyChangeListener(PropertyChangeListener)
     * @see #removeBeanPropertyChangeListener(PropertyChangeListener)
     * @see #getBeanPropertyChangeListeners(String)
     * @see java.beans.PropertyChangeSupport#getPropertyChangeListeners()
     */
    public synchronized PropertyChangeListener[] getBeanPropertyChangeListeners() {
        return beanAdapter.getBeanPropertyChangeListeners();
    }


    /**
     * Returns an array of all the listeners which have been associated
     * with the named property.
     *
     * @param propertyName   the name of the property to lookup listeners
     * @return all of the {@code PropertyChangeListeners} associated with
     *         the named property or an empty array if no listeners have
     *         been added
     *
     * @see #addBeanPropertyChangeListener(String, PropertyChangeListener)
     * @see #removeBeanPropertyChangeListener(String, PropertyChangeListener)
     * @see #getBeanPropertyChangeListeners()
     */
    public synchronized PropertyChangeListener[] getBeanPropertyChangeListeners(String propertyName) {
        return beanAdapter.getBeanPropertyChangeListeners(propertyName);
    }


    // Misc *******************************************************************

    /**
     * Removes the PropertyChangeHandler from the observed bean,
     * if the bean is not {@code null}.
     * Also removes all listeners from the bean that have been registered
     * with {@code #addBeanPropertyChangeListener} before.<p>
     *
     * PresentationModels have a PropertyChangeListener registered with
     * the target bean. Hence, a bean has a reference to all PresentationModels
     * that hold it as bean. To avoid memory leaks it is recommended to remove
     * this listener, if the bean lives much longer than the PresentationModel,
     * enabling the garbage collector to remove the PresentationModel.
     * To do so, you can call {@code setBean(null)} or set the
     * bean channel's value to null.
     * As an alternative you can use event listener lists in your beans
     * that implement references with {@code WeakReference}.<p>
     *
     * Setting the bean to null has side-effects, for example the model
     * fires a change event for the bound property <em>bean</em> and
     * other properties.
     * And the value of ValueModel's vent by this model may change.
     * However, typically this is fine and setting the bean to null
     * is the first choice for removing the reference from the bean to
     * the PresentationModel.
     * Another way to clear the reference from the target bean is to call
     * this {@code #release} method; it has no side-effects.<p>
     *
     * Since version 2.0.4 it is safe to call this method multiple times,
     * however, the PresentationModel must not be used anymore once #release
     * has been called.
     *
     * @see #setBean(Object)
     * @see java.lang.ref.WeakReference
     *
     * @since 1.2
     */
    public void release() {
        beanAdapter.release();
    }

    public ValueModel getBufferedPreferences(String key, Object defaultValue) {
        return getBufferedModel(new MyPreferencesAdapter(key, defaultValue));
    }

    // Helper Class ***********************************************************

    /**
     * Holds a BufferedValueModel together with the names of the getter
     * and setter. Used to look up models in {@code #getBufferedModel}.
     * Also ensures that there are no two buffered models with different
     * getter/setter pairs.
     *
     * @see PresentationModel#getBufferedModel(String)
     * @see PresentationModel#getBufferedModel(String, String, String)
     */
    private static final class WrappedBuffer {

        final BufferedValueModel buffer;
        final String getterName;
        final String setterName;

        WrappedBuffer(
                BufferedValueModel buffer,
                String getterName,
                String setterName) {
            this.buffer = buffer;
            this.getterName = getterName;
            this.setterName = setterName;
        }
    }


    // Event Handling and Forwarding Changes **********************************

    /**
     * Listens to changes of the bean, invoked the before and after methods,
     * and forwards the bean change events.
     */
    private final class BeanChangeHandler implements PropertyChangeListener {

        /**
         * The target bean will change, changes, or has changed.
         *
         * @param evt   the property change event to be handled
         */
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            B oldBean = (B) evt.getOldValue();
            B newBean = (B) evt.getNewValue();
            String propertyName = evt.getPropertyName();
            if (BeanAdapter.PROPERTY_BEFORE_BEAN.equals(propertyName)) {
                beforeBeanChange(oldBean, newBean);
            } else if (BeanAdapter.PROPERTY_BEAN.equals(propertyName)) {
                firePropertyChange(PROPERTY_BEAN, oldBean, newBean, true);
            } else if (BeanAdapter.PROPERTY_AFTER_BEAN.equals(propertyName)) {
                afterBeanChange(oldBean, newBean);
            }
        }
    }


    /**
     * Updates the buffering state if a model buffering state changed.
     */
    private final class BufferingStateHandler implements PropertyChangeListener {

        /**
         * A registered BufferedValueModel has reported a change in its
         * <em>buffering</em> state. Update this model's buffering state.
         *
         * @param evt   describes the property change
         */
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            updateBufferingState(((Boolean) evt.getNewValue()).booleanValue());
        }

    }


    /**
     * Listens to model changes and updates the changed state.
     */
    private final class UpdateHandler implements PropertyChangeListener {

        /**
         * A registered ValueModel has changed.
         * Updates the changed state. If the property that changed is
         * 'changed' we assume that this is another changed state and
         * forward only changes to true. For all other property names,
         * we just update our changed state to true.
         *
         * @param evt   the event that describes the property change
         */
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String propertyName = evt.getPropertyName();
            if (!PROPERTY_CHANGED.equals(propertyName)
                    || ((Boolean) evt.getNewValue()).booleanValue()) {
                setChanged(true);
            }
        }
    }


}
