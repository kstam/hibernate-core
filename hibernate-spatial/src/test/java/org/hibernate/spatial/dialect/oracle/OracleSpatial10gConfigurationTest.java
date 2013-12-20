package org.hibernate.spatial.dialect.oracle;

import org.junit.Assert;
import org.junit.Test;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.service.internal.StandardServiceRegistryImpl;
import org.hibernate.spatial.HibernateSpatialConfiguration;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.ServiceRegistryBuilder;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;

@RequiresDialect(OracleSpatial10gDialect.class)
public class OracleSpatial10gConfigurationTest extends BaseCoreFunctionalTestCase{


	private Configuration addToConfiguration(Configuration cfg, Boolean ogcStrict, String connFinderClassName) {
		cfg.setProperty( HibernateSpatialConfiguration.AvailableSettings.CONNECTION_FINDER, connFinderClassName );
		cfg.setProperty( HibernateSpatialConfiguration.AvailableSettings.OGC_STRICT, ogcStrict.toString() );
		return cfg;
	}

	private void createdOracleSpatialDialect(Boolean ogcStrict, String connFinderClassName) {
		createdOracleSpatialDialect( ogcStrict, connFinderClassName, true );
	}

	private void createdOracleSpatialDialect(Boolean ogcStrict, String connFinderClassName, boolean doConfiguration) {
		Configuration cfg = new Configuration();
		if (doConfiguration){
			addToConfiguration( cfg, ogcStrict, connFinderClassName );
		}
		cfg.setProperty( AvailableSettings.DIALECT, OracleSpatial10gDialect.class.getCanonicalName() );
		StandardServiceRegistryImpl serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( cfg.getProperties() );
		SessionFactory sessionFactory = cfg.buildSessionFactory( serviceRegistry );
		Dialect d = ( (SessionFactoryImpl) sessionFactory ).getDialect();
		OracleSpatial10gDialect osd = (OracleSpatial10gDialect) d;
		Assert.assertTrue( ogcStrict == osd.isOGCStrict() );
		ConnectionFinder finder = osd.getConnectionFinder();
		Assert.assertNotNull( finder );
		Assert.assertEquals( connFinderClassName, finder.getClass().getCanonicalName() );
	}

	@Test
	public void testOgcStrictMockFinder() {
		createdOracleSpatialDialect( true, MockConnectionFinder.class.getCanonicalName() );
	}

	@Test
	public void testOgcNonStrictMockFinder() {
		createdOracleSpatialDialect( false, MockConnectionFinder.class.getCanonicalName() );
	}

	@Test
	public void testOgcStrictDefaultFinder() {
		createdOracleSpatialDialect( true, DefaultConnectionFinder.class.getCanonicalName() );
	}

	@Test
	public void testOgcNonStrictDefaultFinder() {
		createdOracleSpatialDialect( false, DefaultConnectionFinder.class.getCanonicalName() );
	}

	@Test(expected = HibernateException.class)
	public void testOgcStrictNonExistentClass() {
		createdOracleSpatialDialect( false, "doesntexist" );
	}

	@Test
	public void testNoConfiguration(){
		createdOracleSpatialDialect( true, DefaultConnectionFinder.class.getCanonicalName(), false );
	}
}